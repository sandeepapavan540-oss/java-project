import express from 'express';
import db from '../db.js';

const router = express.Router();

router.get('/admin', async (req, res) => {
    try {
   
        const salesQuery = `
            SELECT 
                COUNT(transaction_id) AS total_sales_count,
                IFNULL(SUM(sale_price), 0) AS total_sales_revenue,
                IFNULL(SUM(commission_amount), 0) AS total_platform_commission
            FROM transactions
        `;
        const [salesStats] = await db.query(salesQuery);

    
        const usersQuery = `
            SELECT 
                COUNT(user_id) AS total_users,
                SUM(CASE WHEN user_type = 'SELLER' THEN 1 ELSE 0 END) AS total_sellers,
                SUM(CASE WHEN user_type = 'BUYER' THEN 1 ELSE 0 END) AS total_buyers
            FROM users
        `;
        const [userStats] = await db.query(usersQuery);

    
        const [vehicleStats] = await db.query(
            "SELECT " +
            "SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_vehicles, " +
            "SUM(CASE WHEN status = 'SOLD' THEN 1 ELSE 0 END) AS sold_vehicles " +
            "FROM vehicles"
        );

        res.status(200).json({
            sales: salesStats[0],
            users: userStats[0],
            vehicles: vehicleStats[0]
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while fetching Admin stats.' });
    }
});



router.get('/admin/today', async (req, res) => {
    try {

        const todaySummaryQuery = `
            SELECT
                COUNT(transaction_id) AS today_sales_count,
                IFNULL(SUM(sale_price), 0) AS today_revenue,
                IFNULL(SUM(commission_amount), 0) AS today_commission
            FROM transactions
            WHERE DATE(transaction_date) = CURDATE()
        `;
        const [todaySummary] = await db.query(todaySummaryQuery);

        
        const todayVehiclesQuery = `
            SELECT
                t.transaction_id,
                t.sale_price,
                t.commission_amount,
                t.transaction_date,
                v.brand,
                v.model,
                v.vehicle_type,
                u.username AS buyer_name,
                u.email AS buyer_email
            FROM transactions t
            JOIN vehicles v ON t.vehicle_id = v.vehicle_id
            JOIN users u ON t.buyer_id = u.user_id
            WHERE DATE(t.transaction_date) = CURDATE()
            ORDER BY t.transaction_date DESC
        `;
        const [todayVehicles] = await db.query(todayVehiclesQuery);

        res.status(200).json({
            summary: todaySummary[0],
            sold_today: todayVehicles
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while fetching today\'s sales.' });
    }
});




router.get('/seller/:seller_id', async (req, res) => {
    const { seller_id } = req.params;

    try {
       
        const vehicleQuery = `
            SELECT 
                COUNT(vehicle_id) AS total_listed,
                SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) AS currently_available,
                SUM(CASE WHEN status = 'SOLD' THEN 1 ELSE 0 END) AS total_sold
            FROM vehicles 
            WHERE seller_id = ?
        `;
        const [vehicleStats] = await db.query(vehicleQuery, [seller_id]);

       
        const earningsQuery = `
            SELECT 
                IFNULL(SUM(t.sale_price), 0) AS gross_sales,
                IFNULL(SUM(t.commission_amount), 0) AS commission_paid,
                IFNULL(SUM(t.sale_price - t.commission_amount), 0) AS net_earnings
            FROM transactions t
            JOIN vehicles v ON t.vehicle_id = v.vehicle_id
            WHERE v.seller_id = ?
        `;
        const [earningsStats] = await db.query(earningsQuery, [seller_id]);

        res.status(200).json({
            vehicles: vehicleStats[0],
            earnings: earningsStats[0]
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while fetching Seller stats.' });
    }
});



router.get('/buyer/:buyer_id', async (req, res) => {
    const { buyer_id } = req.params;

    try {
        const query = `
            SELECT 
                t.transaction_id, 
                t.sale_price AS amount_paid, 
                t.transaction_date,
                v.brand, 
                v.model, 
                v.vehicle_type,
                v.images
            FROM transactions t
            JOIN vehicles v ON t.vehicle_id = v.vehicle_id
            WHERE t.buyer_id = ?
            ORDER BY t.transaction_date DESC
        `;
        const [purchases] = await db.query(query, [buyer_id]);

        
        const totalSpent = purchases.reduce((sum, item) => sum + Number(item.amount_paid), 0);

       
        const formattedPurchases = purchases.map(item => ({
            ...item,
            images: item.images ? JSON.parse(item.images) : []
        }));

        res.status(200).json({
            total_purchased_count: formattedPurchases.length,
            total_spent: totalSpent,
            purchase_history: formattedPurchases
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while fetching Buyer stats.' });
    }
});

export default router;
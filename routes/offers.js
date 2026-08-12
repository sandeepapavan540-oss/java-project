import express from 'express';
import db from '../db.js';


const router = express.Router();


router.post('/make', async (req, res) => {
    const { vehicle_id, offer_amount, buyer_id } = req.body;

    if (!vehicle_id || !offer_amount || !buyer_id) {
        return res.status(400).json({ message: 'All fields (vehicle_id, offer_amount, buyer_id) are required!' });
    }

    try {
        const [vehicles] = await db.query('SELECT seller_id, status FROM vehicles WHERE vehicle_id = ?', [vehicle_id]);
        if (vehicles.length === 0) return res.status(404).json({ message: 'Vehicle not found!' });
        
        if (vehicles[0].status === 'SOLD') {
            return res.status(400).json({ message: 'Cannot make an offer. This vehicle is already sold!' });
        }

        const seller_id = vehicles[0].seller_id;

        const query = 'INSERT INTO offers (vehicle_id, buyer_id, seller_id, offer_amount) VALUES (?, ?, ?, ?)';
        await db.query(query, [vehicle_id, buyer_id, seller_id, offer_amount]);

        res.status(201).json({ message: 'Offer submitted successfully!' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while submitting offer.' });
    }
});



router.get('/seller-panel', async (req, res) => {
    const seller_id = req.query.seller_id;

    if (!seller_id) {
        return res.status(400).json({ message: 'seller_id query param is required!' });
    }

    try {
        const query = `
            SELECT o.*, v.brand, v.model, v.price as original_price, u.username as buyer_name 
            FROM offers o
            JOIN vehicles v ON o.vehicle_id = v.vehicle_id
            JOIN users u ON o.buyer_id = u.user_id
            WHERE o.seller_id = ? AND o.status = 'PENDING'
            ORDER BY o.created_at DESC
        `;
        const [offers] = await db.query(query, [seller_id]);
        res.status(200).json(offers);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while fetching seller offers.' });
    }
});


router.get('/all-pending', async (req, res) => {
    try {
        const query = `
            SELECT o.*, v.brand, v.model, v.price AS original_price,
                   ub.username AS buyer_name, us.username AS seller_name
            FROM offers o
            JOIN vehicles v ON o.vehicle_id = v.vehicle_id
            JOIN users ub ON o.buyer_id = ub.user_id
            JOIN users us ON o.seller_id = us.user_id
            WHERE o.status = 'PENDING'
            ORDER BY o.created_at DESC
        `;
        const [offers] = await db.query(query);
        res.status(200).json(offers);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while fetching pending offers.' });
    }
});



router.put('/:id/accept', async (req, res) => {
    const { id } = req.params;
    try {
        await db.query("UPDATE offers SET status = 'ACCEPTED' WHERE offer_id = ?", [id]);
        res.status(200).json({ message: 'Offer accepted successfully!' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while accepting offer.' });
    }
});



router.put('/:id/reject', async (req, res) => {
    const { id } = req.params;
    try {
        await db.query("UPDATE offers SET status = 'REJECTED' WHERE offer_id = ?", [id]);
        res.status(200).json({ message: 'Offer rejected successfully!' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while rejecting offer.' });
    }
});


export default router;
import express from 'express';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import db from '../db.js';
import { generateInvoicePDF } from '../utils/invoiceGenerator.js';
import { sendSellerEmail, sendBuyerEmail } from '../emailService.js';

const router = express.Router();


const uploadsDir = path.join(process.cwd(), 'uploads');
if (!fs.existsSync(uploadsDir)) {
    fs.mkdirSync(uploadsDir, { recursive: true });
}


const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, uploadsDir);
    },
    filename: (req, file, cb) => {
        cb(null, Date.now() + path.extname(file.originalname));
    }
});

const fileFilter = (req, file, cb) => {
    const allowedTypes = /jpeg|jpg|png|webp/;
    const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase());
    const mimetype = allowedTypes.test(file.mimetype);

    if (extname && mimetype) {
        return cb(null, true);
    } else {
        cb(new Error('Only images (jpeg, jpg, png, webp) are allowed!'));
    }
};

const upload = multer({
    storage: storage,
    limits: { fileSize: 5 * 1024 * 1024 },
    fileFilter: fileFilter
}).array('images', 5);



router.post('/register/user', (req, res) => {
    upload(req, res, async (err) => {
        if (err) return res.status(400).json({ message: err.message });

        const { brand, model, price, vehicle_type, seller_id } = req.body;

        if (!brand || !model || !price || !vehicle_type || !seller_id) {
            return res.status(400).json({ message: 'All fields are required!' });
        }

        try {
            const [users] = await db.query('SELECT user_type FROM users WHERE user_id = ?', [seller_id]);
            if (users.length === 0) return res.status(404).json({ message: 'User not found!' });

            if (users[0].user_type !== 'SELLER') {
                return res.status(403).json({ message: 'Access denied! Only Sellers can use this route.' });
            }

            const imageFiles = req.files ? req.files.map(file => file.filename) : [];
            const imagesJson = JSON.stringify(imageFiles);

            const query = 'INSERT INTO vehicles (brand, model, price, vehicle_type, seller_id, images) VALUES (?, ?, ?, ?, ?, ?)';
            await db.query(query, [brand, model, price, vehicle_type, seller_id, imagesJson]);

            res.status(201).json({ message: 'Vehicle registered successfully by Seller with images!' });
        } catch (error) {
            console.error(error);
            res.status(500).json({ message: 'Server error while registering vehicle.' });
        }
    });
});



router.post('/register/admin', (req, res) => {
    upload(req, res, async (err) => {
        if (err) return res.status(400).json({ message: err.message });

        const { brand, model, price, vehicle_type, seller_id } = req.body;

        if (!brand || !model || !price || !vehicle_type || !seller_id) {
            return res.status(400).json({ message: 'All fields are required!' });
        }

        try {
            const [users] = await db.query('SELECT user_type FROM users WHERE user_id = ?', [seller_id]);
            if (users.length === 0) return res.status(404).json({ message: 'Admin user not found!' });

            if (users[0].user_type !== 'ADMIN') {
                return res.status(403).json({ message: 'Access denied! Only Admins can use this route.' });
            }

            const imageFiles = req.files ? req.files.map(file => file.filename) : [];
            const imagesJson = JSON.stringify(imageFiles);

            const query = 'INSERT INTO vehicles (brand, model, price, vehicle_type, seller_id, images) VALUES (?, ?, ?, ?, ?, ?)';
            await db.query(query, [brand, model, price, vehicle_type, seller_id, imagesJson]);

            res.status(201).json({ message: 'Vehicle registered successfully by Admin with images!' });
        } catch (error) {
            console.error(error);
            res.status(500).json({ message: 'Server error while Admin registering vehicle.' });
        }
    });
});



router.get('/all', async (req, res) => {
    try {
        const query = `
            SELECT v.*, u.username AS seller_name
            FROM vehicles v
            JOIN users u ON v.seller_id = u.user_id
            ORDER BY v.vehicle_id DESC
        `;
        const [vehicles] = await db.query(query);

        const formattedVehicles = vehicles.map(vehicle => {
            return {
                ...vehicle,
                images: vehicle.images ? JSON.parse(vehicle.images) : []
            };
        });

        res.status(200).json(formattedVehicles);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while fetching all vehicles.' });
    }
});



router.get('/available', async (req, res) => {
    const { brand, model, vehicle_type, minPrice, maxPrice } = req.query;

    try {
        let query = `
            SELECT v.*, u.username as seller_name FROM vehicles v 
            JOIN users u ON v.seller_id = u.user_id WHERE v.status = 'AVAILABLE'
        `;
        let queryParams = [];

        if (brand) {
            query += ' AND v.brand LIKE ?';
            queryParams.push(`%${brand}%`);
        }
        if (model) {
            query += ' AND v.model LIKE ?';
            queryParams.push(`%${model}%`);
        }
        if (vehicle_type) {
            query += ' AND v.vehicle_type = ?';
            queryParams.push(vehicle_type);
        }
        if (minPrice) {
            query += ' AND v.price >= ?';
            queryParams.push(Number(minPrice));
        }
        if (maxPrice) {
            query += ' AND v.price <= ?';
            queryParams.push(Number(maxPrice));
        }

        query += ' ORDER BY v.vehicle_id DESC';

        const [vehicles] = await db.query(query, queryParams);

        const formattedVehicles = vehicles.map(vehicle => {
            return {
                ...vehicle,
                images: vehicle.images ? JSON.parse(vehicle.images) : []
            };
        });

        res.status(200).json(formattedVehicles);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error while fetching vehicles.' });
    }
});



router.post('/sell', async (req, res) => {
    const { vehicle_id, buyer_id } = req.body;

    if (!vehicle_id || !buyer_id) {
        return res.status(400).json({ message: 'Vehicle ID and Buyer ID are required!' });
    }

    try {
        const vehicleQuery = `
            SELECT v.price, v.status, v.brand, v.model, v.vehicle_type, u.email as seller_email, u.username as seller_name
            FROM vehicles v JOIN users u ON v.seller_id = u.user_id WHERE v.vehicle_id = ?
        `;
        const [vehicles] = await db.query(vehicleQuery, [vehicle_id]);

        if (vehicles.length === 0) return res.status(404).json({ message: 'Vehicle not found!' });
        const vehicle = vehicles[0];

        if (vehicle.status === 'SOLD') return res.status(400).json({ message: 'This vehicle is already sold!' });

        const [buyers] = await db.query('SELECT username, email FROM users WHERE user_id = ?', [buyer_id]);
        if (buyers.length === 0) return res.status(404).json({ message: 'Buyer not found!' });
        const buyer = buyers[0];

        const salePrice = vehicle.price;
        const commission = salePrice * 0.10;

     
        await db.query("UPDATE vehicles SET status = 'SOLD' WHERE vehicle_id = ?", [vehicle_id]);

        
        const transactionQuery = `
            INSERT INTO transactions (vehicle_id, buyer_id, sale_price, commission_amount) VALUES (?, ?, ?, ?)
        `;
        const [result] = await db.query(transactionQuery, [vehicle_id, buyer_id, salePrice, commission]);
        const insertId = result.insertId;

       
        const invoiceFilename = `invoice_${insertId}_${Date.now()}.pdf`;
        const invoicePath = path.join(process.cwd(), 'invoices', invoiceFilename);

        let invoiceGenerated = false;
        try {
            await generateInvoicePDF(insertId, vehicle, buyer, vehicle, salePrice, commission, invoicePath);
            invoiceGenerated = true;
        } catch (invoiceError) {
            console.error('❌ Invoice PDF generation failed:', invoiceError);
        }

        
        let sellerNotified = false;
        let buyerNotified = false;
        try {
            await sendSellerEmail(vehicle.seller_email, vehicle.seller_name, vehicle.brand, vehicle.model, salePrice, commission);
            sellerNotified = true;
        } catch (emailError) {
            console.error(' Seller email failed:', emailError);
        }
        try {
            await sendBuyerEmail(buyer.email, buyer.username, vehicle.brand, vehicle.model, salePrice);
            buyerNotified = true;
        } catch (emailError) {
            console.error(' Buyer email failed:', emailError);
        }

        res.status(200).json({
            message: 'Vehicle transaction completed successfully!',
            details: {
                vehicle: `${vehicle.brand} ${vehicle.model}`,
                sale_price: salePrice,
                commission_10_percent: commission,
                invoice_generated: invoiceGenerated ? invoiceFilename : null,
                seller_notified: sellerNotified ? vehicle.seller_email : null,
                buyer_notified: buyerNotified ? buyer.email : null
            }
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error during the transaction.' });
    }
});

export default router;
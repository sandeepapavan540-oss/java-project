import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import path from 'path';

import authRoutes from './routes/auth.js';
import vehicleRoutes from './routes/vehicles.js';
import dashboardRoutes from './routes/dashboard.js';
import offerRoutes from './routes/offers.js';


dotenv.config();


const app = express();
const PORT = process.env.PORT || 5000;


app.use(cors());
app.use(express.json());


app.use('/uploads', express.static(path.join(process.cwd(), 'uploads')));


app.use('/api/auth', authRoutes);
app.use('/api/vehicles', vehicleRoutes);
app.use('/api/dashboard', dashboardRoutes);
app.use('/api/offers', offerRoutes);


app.get('/', (req, res) => {
    res.send('Vehicle Management System Backend is Clean and Running!');
});


app.listen(PORT, () => {
    console.log(` Server is running on port ${PORT}`);
});
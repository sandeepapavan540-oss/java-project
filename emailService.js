import nodemailer from 'nodemailer';
import dotenv from 'dotenv';

dotenv.config();

const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    },
    
    tls: {
        rejectUnauthorized: false
    }
});


export const sendSellerEmail = async (toEmail, username, brand, model, price, commission) => {
    const mailOptions = {
        from: process.env.EMAIL_USER,
        to: toEmail,
        subject: '🚗 Your Vehicle Has Been Sold! - Vehicle System',
        html: `
            <div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee; border-radius: 10px; max-width: 600px;">
                <h2 style="color: #2ecc71;">Hi ${username}, Great News!</h2>
                <p>Your vehicle has been successfully sold to a buyer through our platform.</p>
                <hr style="border: 0; border-top: 1px solid #eee;" />
                <h3>📊 Sale Summary:</h3>
                <table style="width: 100%; border-collapse: collapse;">
                    <tr><td style="padding: 8px; font-weight: bold;">Vehicle:</td><td style="padding: 8px;">${brand} ${model}</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Total Sale Price:</td><td style="padding: 8px; color: #2c3e50; font-weight: bold;">Rs. ${price.toLocaleString()}</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold; color: #e74c3c;">System Commission (10%):</td><td style="padding: 8px; color: #e74c3c; font-weight: bold;">- Rs. ${commission.toLocaleString()}</td></tr>
                    <tr style="border-top: 2px solid #eee;"><td style="padding: 8px; font-weight: bold; color: #2ecc71;">Your Earnings:</td><td style="padding: 8px; color: #2ecc71; font-weight: bold;">Rs. ${(price - commission).toLocaleString()}</td></tr>
                </table>
                <hr style="border: 0; border-top: 1px solid #eee;" />
                <p style="font-size: 12px; color: #7f8c8d;">Thank you for dealing with us!</p>
            </div>
        `
    };
    try {
        await transporter.sendMail(mailOptions);
        console.log(`📧 Sale confirmation email sent to Seller: ${toEmail}`);
    } catch (error) {
        console.error('❌ Error sending Seller email:', error);
    }
};


export const sendBuyerEmail = async (toEmail, username, brand, model, price) => {
    const mailOptions = {
        from: process.env.EMAIL_USER,
        to: toEmail,
        subject: '🎉 Purchase Confirmation - Vehicle Management System',
        html: `
            <div style="font-family: Arial, sans-serif; padding: 20px; border: 1px solid #eee; border-radius: 10px; max-width: 600px;">
                <h2 style="color: #3498db;">Congratulations ${username}!</h2>
                <p>You have successfully purchased a new vehicle. Here is your purchase receipt.</p>
                <hr style="border: 0; border-top: 1px solid #eee;" />
                <h3>🧾 Purchase Receipt:</h3>
                <table style="width: 100%; border-collapse: collapse;">
                    <tr><td style="padding: 8px; font-weight: bold;">Vehicle Purchased:</td><td style="padding: 8px;">${brand} ${model}</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Amount Paid:</td><td style="padding: 8px; color: #3498db; font-weight: bold;">Rs. ${price.toLocaleString()}</td></tr>
                    <tr><td style="padding: 8px; font-weight: bold;">Status:</td><td style="padding: 8px; color: #2ecc71; font-weight: bold;">PAID & OWNED</td></tr>
                </table>
                <hr style="border: 0; border-top: 1px solid #eee;" />
                <p style="font-size: 12px; color: #7f8c8d;">Please contact the support team or the seller for vehicle delivery and documentation.</p>
            </div>
        `
    };
    try {
        await transporter.sendMail(mailOptions);
        console.log(`📧 Purchase confirmation email sent to Buyer: ${toEmail}`);
    } catch (error) {
        console.error('❌ Error sending Buyer email:', error);
    }
};
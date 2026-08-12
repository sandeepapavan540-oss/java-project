import fs from 'fs';
import path from 'path';
import PDFDocument from 'pdfkit';


export const generateInvoicePDF = (transactionId, vehicle, buyer, seller, salePrice, commission, filePath) => {
    return new Promise((resolve, reject) => {
        try {
         
            const dir = path.dirname(filePath);
            if (!fs.existsSync(dir)) {
                fs.mkdirSync(dir, { recursive: true });
            }

            const doc = new PDFDocument({ margin: 50 });
            const writeStream = fs.createWriteStream(filePath);

           
            writeStream.on('error', (err) => reject(err));
            writeStream.on('finish', () => resolve(filePath));

            doc.pipe(writeStream);

          
            doc.fillColor('#2c3e50').fontSize(24).text('VEHICLE MANAGEMENT SYSTEM', { align: 'center' });
            doc.fontSize(10).fillColor('#7f8c8d').text('Official Transaction Invoice & Receipt', { align: 'center' });
            doc.moveDown(2);

          
            doc.moveTo(50, 100).lineTo(550, 100).stroke('#bdc3c7');
            doc.moveDown(1);

         
            doc.fillColor('#2c3e50').fontSize(12).text(`Invoice ID: INV-${transactionId}`, { weight: 'bold' });
            doc.text(`Date: ${new Date().toLocaleDateString()}`);
            doc.moveDown(1);

    
            doc.fontSize(14).text('Transaction Parties', { underline: true });
            doc.fontSize(11).text(`Buyer Name: ${buyer.username} (${buyer.email})`);
            doc.text(`Seller Name: ${seller.seller_name} (${seller.seller_email})`);
            doc.moveDown(1.5);

         
            doc.fontSize(14).text('Vehicle Details', { underline: true });
            doc.fontSize(11).text(`Vehicle: ${vehicle.brand} ${vehicle.model} (${vehicle.vehicle_type})`);
            doc.moveDown(1.5);

         
            doc.fontSize(14).text('Financial Summary', { underline: true });
            doc.moveDown(0.5);

            doc.fontSize(11).text(`Total Vehicle Price: Rs. ${salePrice.toLocaleString()}`);
            doc.fillColor('#e74c3c').text(`System Commission (10%): Rs. ${commission.toLocaleString()}`);
            doc.fillColor('#2ecc71').fontSize(12).text(`Net Seller Earnings: Rs. ${(salePrice - commission).toLocaleString()}`, { stroke: true });

            doc.moveDown(3);
            doc.moveTo(50, doc.y).lineTo(550, doc.y).stroke('#bdc3c7');
            doc.moveDown(1);

            
            doc.fillColor('#95a5a6').fontSize(10).text('Thank you for choosing our platform. This is a computer-generated receipt.', { align: 'center' });

            doc.end();
        } catch (err) {
            reject(err);
        }
    });
};
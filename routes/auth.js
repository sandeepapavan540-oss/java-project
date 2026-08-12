import express from 'express';
import bcrypt from 'bcrypt';
import db from '../db.js';

const router = express.Router();


router.post('/register', async (req, res) => {
    const { username, email, password, user_type } = req.body;

    if (!username || !email || !password || !user_type) {
        return res.status(400).json({ message: 'All fields are required!' });
    }

    try {
        const [existingUser] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
        if (existingUser.length > 0) {
            return res.status(400).json({ message: 'Email is already registered!' });
        }

        const saltRounds = 10;
        const hashedPassword = await bcrypt.hash(password, saltRounds);

        const query = 'INSERT INTO users (username, email, password, user_type) VALUES (?, ?, ?, ?)';
        await db.query(query, [username, email, hashedPassword, user_type]);

        res.status(201).json({ message: 'User registered successfully!' });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error. Please try again later.' });
    }
});

router.post('/login', async (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ message: 'Please enter both email and password!' });
    }

    try {
        const [users] = await db.query('SELECT * FROM users WHERE email = ?', [email]);
        if (users.length === 0) {
            return res.status(401).json({ message: 'Invalid email or password!' });
        }

        const user = users[0];
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) {
            return res.status(401).json({ message: 'Invalid email or password!' });
        }

       
        res.status(200).json({
            message: 'Login successful!',
            user: {
                user_id: user.user_id,
                username: user.username,
                email: user.email,
                user_type: user.user_type
            }
        });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: 'Server error. Please try again later.' });
    }
});

export default router;
// src/components/pages/Login.jsx
import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

const Login = () => {
    const { login } = useAuth();
    const [credentials, setCredentials] = useState({ username: '', password: '' });

    const handleLogin = async (e) => {
        e.preventDefault();
        const success = await login(credentials);
        if (success) window.location.href = '/learner-dashboard';
    };

    return (
        <form onSubmit={handleLogin}>
            <label>Username</label>
            <input
                type="text"
                value={credentials.username}
                onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
            />
            <label>Password</label>
            <input
                type="password"
                value={credentials.password}
                onChange={(e) =>
                    setCredentials({ ...credentials, password: e.target.value })
                }
            />
            <button type="submit">Login</button>
        </form>
    );
};

export default Login;
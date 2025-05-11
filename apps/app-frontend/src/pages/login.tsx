import React, { useState } from 'react';
import { useRouter } from 'next/router';
import api from '@/lib/api';
import { useAuth } from '@/context/AuthContext';
import toast from "react-hot-toast";

export default function LoginPage() {
    const [identifier, setIdentifier] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const router = useRouter();

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const response = await api.post('/api/auth/login', { identifier, password });
            const { token } = response.data;
            login(token);
            toast.success('Login successful');
            await router.push('/dashboard');
        } catch (err: any) {
            const message = err.response?.data || 'Invalid credentials';
            toast.error(typeof message === 'string' ? message : 'Login failed');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-background text-text px-4">
            <form
                onSubmit={handleLogin}
                className="bg-[#111827] rounded-lg p-8 shadow-xl w-full max-w-md"
            >
                <h1 className="text-2xl font-semibold text-primary mb-6">Sign in to Trix</h1>

                <input
                    type="text"
                    placeholder="Email or Username"
                    value={identifier}
                    onChange={(e) => setIdentifier(e.target.value)}
                    required
                    disabled={loading}
                    className="w-full mb-4 p-3 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-primary"
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    disabled={loading}
                    className="w-full mb-4 p-3 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-primary"
                />

                {error && <p className="text-red-500 mb-4">{error}</p>}

                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-primary text-white py-2 rounded hover:bg-blue-500 transition disabled:opacity-50"
                >
                    {loading ? 'Signing in...' : 'Login'}
                </button>
            </form>
        </div>
    );
}

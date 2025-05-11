import React, { useState } from 'react';
import { useRouter } from 'next/router';
import api from '@/lib/api';
import toast from "react-hot-toast";

export default function RegisterPage() {
    const [form, setForm] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const router = useRouter();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await api.post('/api/auth/register', form);
            toast.success('Account created successfully');
            await router.push('/login');
        } catch (err: any) {
            const message = err.response?.data || 'Registration failed';
            toast.error(typeof message === 'string' ? message : 'Error occurred');
        }
 finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center bg-background text-text px-4">
            <form
                onSubmit={handleRegister}
                className="bg-[#111827] rounded-lg p-8 shadow-xl w-full max-w-md"
            >
                <h1 className="text-2xl font-semibold text-primary mb-6">Create an Account</h1>

                <input
                    type="text"
                    name="username"
                    placeholder="Username"
                    value={form.username}
                    onChange={handleChange}
                    required
                    disabled={loading}
                    className="w-full mb-4 p-3 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-primary"
                />

                <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    value={form.email}
                    onChange={handleChange}
                    required
                    disabled={loading}
                    className="w-full mb-4 p-3 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-primary"
                />

                <input
                    type="password"
                    name="password"
                    placeholder="Password"
                    value={form.password}
                    onChange={handleChange}
                    required
                    disabled={loading}
                    className="w-full mb-4 p-3 rounded bg-gray-800 text-white border border-gray-700 focus:outline-none focus:ring-2 focus:ring-primary"
                />

                <input
                    type="password"
                    name="confirmPassword"
                    placeholder="Confirm Password"
                    value={form.confirmPassword}
                    onChange={handleChange}
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
                    {loading ? 'Creating account...' : 'Register'}
                </button>
            </form>
        </div>
    );
}

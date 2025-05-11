import { useEffect, useState } from 'react';
import withAuth from '@/lib/withAuth';
import api from '@/lib/api';

type UserData = {
    id: number;
    username: string;
    email: string;
    roles: string[];
};

function MePage() {
    const [user, setUser] = useState<UserData | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.get('/api/auth/me')
            .then((res) => setUser(res.data))
            .catch(() => setUser(null))
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-background text-text">
                <p className="text-lg text-primary">Loading profile...</p>
            </div>
        );
    }

    if (!user) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-background text-text">
                <p className="text-red-500">Failed to load profile.</p>
            </div>
        );
    }

    return (
        <div className="p-8 max-w-xl mx-auto bg-[#111827] rounded-lg shadow-md text-white">
            <h1 className="text-3xl font-bold text-primary mb-6">Your Profile</h1>

            <div className="flex items-center space-x-4 mb-4">
                {/* Avatar Placeholder */}
                <div className="h-16 w-16 rounded-full bg-gray-700 flex items-center justify-center text-2xl text-white">
                    {user.username[0].toUpperCase()}
                </div>
                <div>
                    <p className="text-xl font-semibold">{user.username}</p>
                    <p className="text-sm text-gray-300">{user.email}</p>
                </div>
            </div>

            <p className="mt-2"><strong>Role:</strong> {user.roles.join(', ')}</p>
        </div>
    );
}

export default withAuth(MePage);

import withAuth from '@/lib/withAuth';
import { useAuth } from '@/context/AuthContext';

function MePage() {
    const { token } = useAuth();

    // For now, we'll just show the JWT payload as a placeholder
    const decoded = token
        ? JSON.parse(atob(token.split('.')[1]))
        : null;

    return (
        <div className="p-8 max-w-xl mx-auto bg-[#111827] rounded-lg shadow-md text-white">
            <h1 className="text-3xl font-bold text-primary mb-4">Your Profile</h1>

            {decoded ? (
                <div className="space-y-2">
                    <p><strong>Username:</strong> {decoded.sub || decoded.username}</p>
                    <p><strong>Email:</strong> {decoded.email || 'N/A'}</p>
                    <p><strong>Role:</strong> {decoded.role || 'User'}</p>
                </div>
            ) : (
                <p className="text-gray-400">Unable to load profile.</p>
            )}
        </div>
    );
}

export default withAuth(MePage);

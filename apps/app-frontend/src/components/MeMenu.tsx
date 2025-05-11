import { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/router';
import toast from 'react-hot-toast';
import { ChevronDownIcon } from '@heroicons/react/24/solid';

export default function MeMenu() {
    const [open, setOpen] = useState(false);
    const { logout } = useAuth();
    const router = useRouter();

    const handleLogout = () => {
        logout();
        toast.success('Logged out');
    };

    return (
        <div className="relative inline-block text-left">
            <button
                onClick={() => setOpen(!open)}
                className="flex items-center space-x-1 bg-gray-800 text-white px-3 py-2 rounded hover:bg-gray-700 transition"
            >
                <span>Me</span>
                <ChevronDownIcon className="w-4 h-4" />
            </button>

            {open && (
                <div className="absolute right-0 mt-2 w-40 bg-white text-black rounded shadow-lg z-50">
                    <button
                        className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                        onClick={() => router.push('/me')}
                    >
                        View Profile
                    </button>
                    <button
                        className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </div>
            )}
        </div>
    );
}

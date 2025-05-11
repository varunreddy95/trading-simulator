import React, { useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '@/context/AuthContext';
import { ArrowPathIcon } from '@heroicons/react/24/outline';
import type { JSX } from 'react';

export default function withAuth<P extends JSX.IntrinsicAttributes>(
    WrappedComponent: React.ComponentType<P>
) {
    return function ProtectedComponent(props: P) {
        const { isAuthenticated } = useAuth();
        const router = useRouter();

        useEffect(() => {
            if (!isAuthenticated) {
                void router.push('/login');
            }
        }, [isAuthenticated]);

        if (!isAuthenticated) {
            return (
                <div className="min-h-screen flex items-center justify-center bg-background text-text">
                    <ArrowPathIcon className="h-8 w-8 animate-spin text-primary" />
                </div>
            );
        }

        return <WrappedComponent {...props} />;
    };
}

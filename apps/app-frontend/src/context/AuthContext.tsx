import { createContext, useContext, useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import { setAuthToken } from '@/lib/api';

type AuthContextType = {
    token: string | null;
    login: (token: string) => void;
    logout: () => void;
    isAuthenticated: boolean;
};

const AuthContext = createContext<AuthContextType>({
    token: null,
    login: () => {},
    logout: () => {},
    isAuthenticated: false,
});

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
    const [token, setToken] = useState<string | null>(null);
    const router = useRouter();

    useEffect(() => {
        const stored = localStorage.getItem('token');
        if (stored) {
            setToken(stored);
            setAuthToken(stored);
        }
    }, []);

    const login = (jwt: string) => {
        localStorage.setItem('token', jwt);
        setToken(jwt);
        setAuthToken(jwt);
    };

    const logout = () => {
        localStorage.removeItem('token');
        setToken(null);
        setAuthToken(null);
        router.push('/login');
    };

    return (
        <AuthContext.Provider value={{ token, login, logout, isAuthenticated: !!token }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);

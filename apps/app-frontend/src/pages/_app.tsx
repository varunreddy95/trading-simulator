import "@/styles/globals.css";
import type { AppProps } from "next/app";
import Navbar from '@/components/Navbar';
import {AuthProvider} from "@/context/AuthContext";
import { Toaster } from 'react-hot-toast';

export default function App({ Component, pageProps }: AppProps) {
    return (
        <AuthProvider>
            <Navbar />
            <Toaster
                position="top-right"
                toastOptions={{
                    style: {
                        background: '#1A1A1A',
                        color: '#E6F0FF',
                        border: '1px solid #2F80ED',
                    },
                    success: {
                        style: {
                            border: '1px solid #27AE60',
                            background: '#1A1A1A',
                            color: '#E6F0FF',
                        },
                        iconTheme: {
                            primary: '#27AE60',
                            secondary: '#1A1A1A',
                        },
                    },
                    error: {
                        style: {
                            border: '1px solid #FF4D4F',
                            background: '#1A1A1A',
                            color: '#E6F0FF',
                        },
                        iconTheme: {
                            primary: '#FF4D4F',
                            secondary: '#1A1A1A',
                        },
                    },
                }}
            />

            <main className="min-h-screen bg-background text-text">
                <Component {...pageProps} />
            </main>
        </AuthProvider>
    );
}

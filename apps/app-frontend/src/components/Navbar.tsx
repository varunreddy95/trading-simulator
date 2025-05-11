import Image from 'next/image';
import Link from 'next/link';
import {useAuth} from "@/context/AuthContext";
import MeMenu from "@/components/MeMenu";

export default function Navbar() {
    const {isAuthenticated} = useAuth();

    return (
        <nav className="bg-background text-text shadow-md px-6 py-4 flex items-center justify-between">
            <Link href="/" className="flex items-center space-x-2">
                <Image src="/trix-main-logo.png" alt="Trix" width={140} height={40} priority/>
            </Link>

            <div className="space-x-6 hidden sm:flex">
                {isAuthenticated ? (
                    <MeMenu/>
                ) : (
                    <>
                        <Link href="/login" className="hover:text-primary transition">Login</Link>
                        <Link href="/register" className="hover:text-primary transition">Register</Link>
                    </>
                )}
            </div>
        </nav>
    );
}
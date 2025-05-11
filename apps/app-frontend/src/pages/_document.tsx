import { Html, Head, Main, NextScript } from 'next/document';

export default function Document() {
    return (
        <Html lang="en">
            <Head>
                {/* ✅ Favicon */}
                <link rel="icon" href="/favicon.png" />
            </Head>
            <body className="bg-background text-text">
            <Main />
            <NextScript />
            </body>
        </Html>
    );
}

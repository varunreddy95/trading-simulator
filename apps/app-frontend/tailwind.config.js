/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ['./src/**/*.{js,ts,jsx,tsx}'],
    theme: {
        extend: {
            colors: {
                primary: '#2F80ED',
                secondary: '#56CCF2',
                background: '#F9F9F9',
                accent: '#27AE60',
                text: '#1F2937',
            },
        },
    },
    plugins: [],
};

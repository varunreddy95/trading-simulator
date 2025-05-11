import axios from 'axios';

const api = axios.create({
    baseURL: process.env.NEXT_PUBLIC_API_URL,
    withCredentials: false, // set to true if using cookies later
    headers: {
        'Content-Type': 'application/json',
    },
});

// Optional: Add token if needed globally
export function setAuthToken(token: string | null) {
    if (token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    } else {
        delete api.defaults.headers.common['Authorization'];
    }
}

export default api;

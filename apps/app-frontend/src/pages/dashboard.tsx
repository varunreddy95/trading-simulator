import withAuth from '@/lib/withAuth';

function DashboardPage() {
    return (
        <div className="p-8">
            <h1 className="text-3xl font-bold text-primary">Welcome to your dashboard</h1>
            <p className="mt-4 text-text">Here’s where your portfolio will live.</p>
        </div>
    );
}

export default withAuth(DashboardPage);

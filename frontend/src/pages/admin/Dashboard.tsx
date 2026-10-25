import { useEffect, useState } from 'react';
import { Users, TrendingUp, DollarSign, Activity } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import api from '../../lib/axios';
import { AdminDashboard as AdminDashboardType } from '../../types';
import { formatNumber, formatCurrency } from '../../lib/utils';
import toast from 'react-hot-toast';

export default function AdminDashboard() {
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState<AdminDashboardType | null>(null);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      setLoading(true);
      const response = await api.get<AdminDashboardType>('/admin/dashboard');
      setDashboard(response.data);
    } catch (error) {
      toast.error('Không thể tải dữ liệu dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
        <p className="text-gray-500 mt-1">Tổng quan hệ thống</p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center">
              <Users className="w-6 h-6 text-primary" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Tổng người dùng</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatNumber(dashboard?.totalUsers || 0)}
              </div>
              {dashboard && dashboard.userGrowth > 0 && (
                <div className="text-xs text-success flex items-center gap-1 mt-1">
                  <TrendingUp className="w-3 h-3" />
                  +{dashboard.userGrowth}% so với tháng trước
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-lg bg-success/10 flex items-center justify-center">
              <Activity className="w-6 h-6 text-success" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Người dùng hoạt động</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatNumber(dashboard?.activeUsers || 0)}
              </div>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-lg bg-warning/10 flex items-center justify-center">
              <DollarSign className="w-6 h-6 text-warning" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Tổng giao dịch</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatNumber(dashboard?.totalTransactions || 0)}
              </div>
              {dashboard && dashboard.transactionGrowth > 0 && (
                <div className="text-xs text-success flex items-center gap-1 mt-1">
                  <TrendingUp className="w-3 h-3" />
                  +{dashboard.transactionGrowth}% so với tháng trước
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-lg bg-danger/10 flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-danger" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Tổng doanh thu</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(dashboard?.totalRevenue || 0)}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* User Growth Chart */}
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 mb-6">
          Tăng trưởng người dùng
        </h2>
        <div className="h-80">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={dashboard?.usersByMonth || []}>
              <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip />
              <Line
                type="monotone"
                dataKey="count"
                stroke="#5B4FFF"
                strokeWidth={2}
                dot={{ fill: '#5B4FFF', r: 4 }}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Recent Users */}
      <div className="card">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">
          Người dùng mới nhất
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-200">
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                  Người dùng
                </th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                  Email
                </th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                  Vai trò
                </th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                  Trạng thái
                </th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                  Ngày tạo
                </th>
              </tr>
            </thead>
            <tbody>
              {dashboard?.recentUsers.map((user) => (
                <tr key={user.id} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-primary flex items-center justify-center text-white font-medium">
                        {user.fullName.charAt(0)}
                      </div>
                      <div>
                        <div className="font-medium text-sm text-gray-900">
                          {user.fullName}
                        </div>
                        <div className="text-xs text-gray-500">{user.username}</div>
                      </div>
                    </div>
                  </td>
                  <td className="py-3 px-4 text-sm text-gray-600">{user.email}</td>
                  <td className="py-3 px-4">
                    <span
                      className={`badge ${
                        user.role === 'ADMIN' ? 'badge-danger' : 'badge-primary'
                      }`}
                    >
                      {user.role}
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <span
                      className={`badge ${
                        user.isActive ? 'badge-success' : 'badge-danger'
                      }`}
                    >
                      {user.isActive ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-sm text-gray-600">
                    {new Date(user.createdAt).toLocaleDateString('vi-VN')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

import { useEffect, useState } from 'react';
import { ArrowUpRight, ArrowDownRight, CreditCard, TrendingUp, Plus } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { useSpaceStore } from '../../store/spaceStore';
import api from '../../lib/axios';
import { DashboardResponse, Transaction } from '../../types';
import { formatCurrency, formatDate, getMonthName } from '../../lib/utils';
import toast from 'react-hot-toast';

export default function Overview() {
  const { currentSpace } = useSpaceStore();
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);
  const [period, setPeriod] = useState<'daily' | 'weekly' | 'monthly'>('monthly');
  
  const currentDate = new Date();
  const month = currentDate.getMonth() + 1;
  const year = currentDate.getFullYear();

  useEffect(() => {
    if (currentSpace) {
      fetchDashboard();
    }
  }, [currentSpace]);

  const fetchDashboard = async () => {
    if (!currentSpace) return;
    
    try {
      setLoading(true);
      const response = await api.get<DashboardResponse>(
        `/reports/dashboard?spaceId=${currentSpace.id}&month=${month}&year=${year}`
      );
      setDashboard(response.data);
    } catch (error: any) {
      toast.error('Không thể tải dữ liệu dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (!currentSpace) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="text-center">
          <p className="text-gray-500 mb-4">Vui lòng chọn không gian tài chính</p>
        </div>
      </div>
    );
  }

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
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Overview</h1>
          <p className="text-gray-500 mt-1">
            Track your income & expenses to reach your personal goal
          </p>
        </div>
        <button className="btn btn-primary flex items-center gap-2">
          <Plus className="w-5 h-5" />
          New budget
        </button>
      </div>

      {/* Balance Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Total Balance */}
        <div className="card">
          <div className="text-sm text-gray-600 mb-2">Your total balance</div>
          <div className="text-3xl font-bold text-gray-900 mb-4">
            {formatCurrency(dashboard?.totalBalance || 0)}
          </div>
          <div className="flex items-center gap-4 text-sm">
            <div className="flex items-center gap-1">
              <span className="text-gray-600">+{formatCurrency(dashboard?.monthIncome || 0)}</span>
              <span className="text-gray-400">Monthly income</span>
            </div>
            <div className="flex items-center gap-1">
              <span className="text-gray-600">-{formatCurrency(dashboard?.monthExpense || 0)}</span>
              <span className="text-gray-400">Monthly spending</span>
            </div>
          </div>
        </div>

        {/* VISA Card */}
        <div className="card bg-gradient-to-br from-primary to-primary-dark text-white relative overflow-hidden">
          <div className="relative z-10">
            <div className="flex justify-between items-start mb-8">
              <div className="text-sm opacity-90">{formatCurrency(dashboard?.monthIncome || 0)}</div>
              <div className="text-sm font-medium">VISA</div>
            </div>
            <div className="mb-4">
              <div className="text-sm opacity-75 mb-1">Doc Bowley</div>
              <div className="text-xs opacity-60">**** 3456</div>
            </div>
          </div>
          <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -mr-16 -mt-16"></div>
          <div className="absolute bottom-0 left-0 w-24 h-24 bg-white/10 rounded-full -ml-12 -mb-12"></div>
        </div>

        {/* Savings Card */}
        <div className="card bg-gradient-to-br from-success to-success-dark text-white relative overflow-hidden">
          <div className="relative z-10">
            <div className="flex justify-between items-start mb-8">
              <div className="text-sm opacity-90">{formatCurrency(dashboard?.monthSavings || 0)}</div>
              <div className="text-sm font-medium">VISA</div>
            </div>
            <div className="mb-4">
              <div className="text-sm opacity-75 mb-1">Doc Bowley</div>
              <div className="text-xs opacity-60">**** 3456</div>
            </div>
          </div>
          <div className="absolute top-0 right-0 w-32 h-32 bg-white/10 rounded-full -mr-16 -mt-16"></div>
          <div className="absolute bottom-0 left-0 w-24 h-24 bg-white/10 rounded-full -ml-12 -mb-12"></div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Chart Section */}
        <div className="lg:col-span-2 card">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-lg font-semibold text-gray-900">Total spent</h2>
              <div className="flex items-center gap-2 mt-1">
                <span className="text-2xl font-bold text-gray-900">
                  {formatCurrency(dashboard?.monthExpense || 0)}
                </span>
              </div>
            </div>
            <div className="flex gap-2">
              <button
                onClick={() => setPeriod('daily')}
                className={`px-3 py-1.5 text-sm font-medium rounded-lg transition-colors ${
                  period === 'daily' ? 'bg-gray-900 text-white' : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                Daily
              </button>
              <button
                onClick={() => setPeriod('weekly')}
                className={`px-3 py-1.5 text-sm font-medium rounded-lg transition-colors ${
                  period === 'weekly' ? 'bg-gray-900 text-white' : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                Weekly
              </button>
              <button
                onClick={() => setPeriod('monthly')}
                className={`px-3 py-1.5 text-sm font-medium rounded-lg transition-colors ${
                  period === 'monthly' ? 'bg-gray-900 text-white' : 'text-gray-600 hover:bg-gray-100'
                }`}
              >
                Monthly
              </button>
              <button className="px-3 py-1.5 text-sm font-medium text-gray-600 hover:bg-gray-100 rounded-lg">
                Custom
              </button>
            </div>
          </div>

          {/* Chart */}
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={dashboard?.dailyExpenses || []}>
                <defs>
                  <linearGradient id="colorExpense" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#5B4FFF" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#5B4FFF" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis 
                  dataKey="date" 
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => {
                    const date = new Date(value);
                    return `${date.getDate()}/${date.getMonth() + 1}`;
                  }}
                />
                <YAxis 
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => `${(value / 1000).toFixed(0)}K`}
                />
                <Tooltip 
                  formatter={(value: number) => formatCurrency(value)}
                  labelFormatter={(label) => formatDate(label)}
                />
                <Area 
                  type="monotone" 
                  dataKey="amount" 
                  stroke="#5B4FFF" 
                  strokeWidth={2}
                  fillOpacity={1} 
                  fill="url(#colorExpense)" 
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Categories & Goals */}
        <div className="space-y-6">
          {/* Categories */}
          <div className="card">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold text-gray-900">Categories</h3>
              <button className="text-sm text-primary hover:text-primary-dark">Edit</button>
            </div>
            <div className="space-y-3">
              {dashboard?.categoryExpenses?.slice(0, 4).map((cat, index) => (
                <div key={index} className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                    index === 0 ? 'bg-orange-100' :
                    index === 1 ? 'bg-pink-100' :
                    index === 2 ? 'bg-purple-100' :
                    'bg-green-100'
                  }`}>
                    <span className="text-lg">{cat.categoryIcon || '🍔'}</span>
                  </div>
                  <div className="flex-1">
                    <div className="font-medium text-sm text-gray-900">{cat.categoryName}</div>
                    <div className="text-xs text-gray-500">{formatCurrency(cat.amount)}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Savings Goal */}
          <div className="card bg-gradient-to-br from-primary to-purple-600 text-white">
            <div className="flex items-start justify-between mb-4">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-2">
                  <span className="text-2xl">👍</span>
                  <span className="text-sm font-medium">You are on track</span>
                </div>
                <div className="text-3xl font-bold mb-1">
                  {formatCurrency(dashboard?.monthSavings || 0)}
                </div>
                <div className="text-sm opacity-90">Left to spend</div>
              </div>
            </div>
            <div className="mt-4 pt-4 border-t border-white/20">
              <div className="flex items-center justify-between">
                <span className="text-sm opacity-90">Monthly Budget</span>
                <span className="text-sm font-medium badge badge-success">On track</span>
              </div>
            </div>
          </div>

          {/* Bot Activation */}
          <div className="card bg-gradient-to-br from-blue-500 to-purple-600 text-white text-center">
            <h3 className="font-semibold mb-2">Activate bot</h3>
            <p className="text-sm opacity-90 mb-4">
              Activate bot kiro to get real-time charges to working (beta)
            </p>
            <div className="mb-4">
              <div className="w-16 h-16 mx-auto bg-white/20 rounded-full flex items-center justify-center">
                <span className="text-3xl">🤖</span>
              </div>
            </div>
            <button className="btn bg-white text-primary hover:bg-gray-100 w-full">
              Get started
            </button>
          </div>
        </div>
      </div>

      {/* Activities Table */}
      <div className="card">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-gray-900">Activities</h2>
          <button className="text-sm text-primary hover:text-primary-dark">View all</button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-gray-200">
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">INCOME</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">DATE</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">CATEGORY</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">AMOUNT</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-gray-600">BALANCE</th>
              </tr>
            </thead>
            <tbody>
              {dashboard?.recentTransactions?.map((transaction) => (
                <tr key={transaction.id} className="border-b border-gray-100 hover:bg-gray-50">
                  <td className="py-3 px-4">
                    <div className="flex items-center gap-3">
                      <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${
                        transaction.type === 'INCOME' ? 'bg-success/10' : 'bg-danger/10'
                      }`}>
                        <span className="text-sm">{transaction.categoryIcon || '💰'}</span>
                      </div>
                      <div>
                        <div className="font-medium text-sm text-gray-900">
                          {transaction.description || transaction.categoryName}
                        </div>
                        <div className="text-xs text-gray-500">{transaction.createdBy}</div>
                      </div>
                    </div>
                  </td>
                  <td className="py-3 px-4 text-sm text-gray-600">
                    {formatDate(transaction.transactionDate)}
                  </td>
                  <td className="py-3 px-4">
                    <span className={`badge ${
                      transaction.type === 'INCOME' ? 'badge-success' : 'badge-danger'
                    }`}>
                      {transaction.categoryName}
                    </span>
                  </td>
                  <td className="py-3 px-4">
                    <span className={`font-medium ${
                      transaction.type === 'INCOME' ? 'text-success' : 'text-danger'
                    }`}>
                      {transaction.type === 'INCOME' ? '+' : '-'}
                      {formatCurrency(transaction.amount)}
                    </span>
                  </td>
                  <td className="py-3 px-4 text-right font-medium text-gray-900">
                    {formatCurrency(currentSpace.balance)}
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

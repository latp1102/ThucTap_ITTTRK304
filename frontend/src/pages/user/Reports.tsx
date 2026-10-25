import { useEffect, useState } from 'react';
import { TrendingUp, TrendingDown, DollarSign } from 'lucide-react';
import {
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';
import { useSpaceStore } from '../../store/spaceStore';
import api from '../../lib/axios';
import { formatCurrency } from '../../lib/utils';
import toast from 'react-hot-toast';

interface ReportData {
  totalIncome: number;
  totalExpense: number;
  netSavings: number;
  categoryExpenses: Array<{
    categoryName: string;
    amount: number;
  }>;
  monthlyTrend: Array<{
    month: string;
    income: number;
    expense: number;
  }>;
}

const COLORS = ['#5B4FFF', '#2DD4BF', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899'];

export default function Reports() {
  const { currentSpace } = useSpaceStore();
  const [loading, setLoading] = useState(true);
  const [reportData, setReportData] = useState<ReportData | null>(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  useEffect(() => {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    setStartDate(firstDay.toISOString().split('T')[0]);
    setEndDate(today.toISOString().split('T')[0]);
  }, []);

  useEffect(() => {
    if (currentSpace && startDate && endDate) {
      fetchReport();
    }
  }, [currentSpace, startDate, endDate]);

  const fetchReport = async () => {
    if (!currentSpace) return;

    try {
      setLoading(true);
      const response = await api.get(
        `/reports?spaceId=${currentSpace.id}&startDate=${startDate}&endDate=${endDate}`
      );
      setReportData(response.data);
    } catch (error) {
      toast.error('Không thể tải báo cáo');
    } finally {
      setLoading(false);
    }
  };

  if (!currentSpace) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-gray-500">Vui lòng chọn không gian tài chính</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Financial Insight</h1>
          <p className="text-gray-500 mt-1">Phân tích chi tiết tình hình tài chính</p>
        </div>
        <div className="flex gap-3">
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="input"
          />
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            className="input"
          />
        </div>
      </div>

      {loading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
        </div>
      ) : (
        <>
          {/* Summary Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="card">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-lg bg-success/10 flex items-center justify-center">
                  <TrendingUp className="w-6 h-6 text-success" />
                </div>
                <div>
                  <div className="text-sm text-gray-600">Tổng thu nhập</div>
                  <div className="text-2xl font-bold text-gray-900">
                    {formatCurrency(reportData?.totalIncome || 0)}
                  </div>
                </div>
              </div>
            </div>

            <div className="card">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-lg bg-danger/10 flex items-center justify-center">
                  <TrendingDown className="w-6 h-6 text-danger" />
                </div>
                <div>
                  <div className="text-sm text-gray-600">Tổng chi tiêu</div>
                  <div className="text-2xl font-bold text-gray-900">
                    {formatCurrency(reportData?.totalExpense || 0)}
                  </div>
                </div>
              </div>
            </div>

            <div className="card">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center">
                  <DollarSign className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <div className="text-sm text-gray-600">Tiết kiệm ròng</div>
                  <div
                    className={`text-2xl font-bold ${
                      (reportData?.netSavings || 0) >= 0
                        ? 'text-success'
                        : 'text-danger'
                    }`}
                  >
                    {formatCurrency(reportData?.netSavings || 0)}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Charts */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Category Expenses Pie Chart */}
            <div className="card">
              <h2 className="text-lg font-semibold text-gray-900 mb-6">
                Chi tiêu theo danh mục
              </h2>
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={reportData?.categoryExpenses || []}
                    dataKey="amount"
                    nameKey="categoryName"
                    cx="50%"
                    cy="50%"
                    outerRadius={100}
                    label={(entry) => entry.categoryName}
                  >
                    {reportData?.categoryExpenses.map((entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={COLORS[index % COLORS.length]}
                      />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value: number) => formatCurrency(value)} />
                </PieChart>
              </ResponsiveContainer>
            </div>

            {/* Monthly Trend Bar Chart */}
            <div className="card">
              <h2 className="text-lg font-semibold text-gray-900 mb-6">
                Xu hướng theo tháng
              </h2>
              <ResponsiveContainer width="100%" height={300}>
                <BarChart data={reportData?.monthlyTrend || []}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis tickFormatter={(value) => `${(value / 1000000).toFixed(0)}M`} />
                  <Tooltip formatter={(value: number) => formatCurrency(value)} />
                  <Legend />
                  <Bar dataKey="income" fill="#2DD4BF" name="Thu nhập" />
                  <Bar dataKey="expense" fill="#EF4444" name="Chi tiêu" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          {/* Category Details Table */}
          <div className="card">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">
              Chi tiết theo danh mục
            </h2>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                      Danh mục
                    </th>
                    <th className="text-right py-3 px-4 text-sm font-medium text-gray-600">
                      Số tiền
                    </th>
                    <th className="text-right py-3 px-4 text-sm font-medium text-gray-600">
                      % Tổng chi tiêu
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {reportData?.categoryExpenses.map((cat, index) => (
                    <tr key={index} className="border-b border-gray-100">
                      <td className="py-3 px-4 flex items-center gap-2">
                        <div
                          className="w-3 h-3 rounded-full"
                          style={{ backgroundColor: COLORS[index % COLORS.length] }}
                        />
                        <span className="text-sm text-gray-900">{cat.categoryName}</span>
                      </td>
                      <td className="py-3 px-4 text-right font-medium text-gray-900">
                        {formatCurrency(cat.amount)}
                      </td>
                      <td className="py-3 px-4 text-right text-sm text-gray-600">
                        {((cat.amount / (reportData?.totalExpense || 1)) * 100).toFixed(
                          1
                        )}
                        %
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

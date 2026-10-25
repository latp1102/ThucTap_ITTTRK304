import { useEffect, useState } from 'react';
import { Plus, TrendingUp } from 'lucide-react';
import { useSpaceStore } from '../../store/spaceStore';
import api from '../../lib/axios';
import { Budget as BudgetType } from '../../types';
import { formatCurrency, calculatePercentage } from '../../lib/utils';
import toast from 'react-hot-toast';

export default function Budget() {
  const { currentSpace } = useSpaceStore();
  const [budgets, setBudgets] = useState<BudgetType[]>([]);
  const [loading, setLoading] = useState(true);
  
  const currentDate = new Date();
  const [month, setMonth] = useState(currentDate.getMonth() + 1);
  const [year, setYear] = useState(currentDate.getFullYear());

  useEffect(() => {
    if (currentSpace) {
      fetchBudgets();
    }
  }, [currentSpace, month, year]);

  const fetchBudgets = async () => {
    if (!currentSpace) return;

    try {
      setLoading(true);
      const response = await api.get<BudgetType[]>(
        `/budgets?spaceId=${currentSpace.id}&month=${month}&year=${year}`
      );
      setBudgets(response.data);
    } catch (error) {
      toast.error('Không thể tải danh sách ngân sách');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (percent: number) => {
    if (percent >= 90) return 'text-danger bg-danger/10';
    if (percent >= 70) return 'text-warning bg-warning/10';
    return 'text-success bg-success/10';
  };

  const getProgressColor = (percent: number) => {
    if (percent >= 90) return 'bg-danger';
    if (percent >= 70) return 'bg-warning';
    return 'bg-success';
  };

  if (!currentSpace) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-gray-500">Vui lòng chọn không gian tài chính</p>
      </div>
    );
  }

  const totalBudget = budgets.reduce((sum, b) => sum + b.amount, 0);
  const totalSpent = budgets.reduce((sum, b) => sum + b.spent, 0);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Budget</h1>
          <p className="text-gray-500 mt-1">Quản lý ngân sách theo danh mục</p>
        </div>
        <div className="flex gap-3">
          <select
            value={month}
            onChange={(e) => setMonth(parseInt(e.target.value))}
            className="input"
          >
            {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
              <option key={m} value={m}>
                Tháng {m}
              </option>
            ))}
          </select>
          <select
            value={year}
            onChange={(e) => setYear(parseInt(e.target.value))}
            className="input"
          >
            {Array.from({ length: 5 }, (_, i) => currentDate.getFullYear() - 2 + i).map((y) => (
              <option key={y} value={y}>
                {y}
              </option>
            ))}
          </select>
          <button className="btn btn-primary flex items-center gap-2">
            <Plus className="w-5 h-5" />
            Thêm ngân sách
          </button>
        </div>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-primary" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Tổng ngân sách</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(totalBudget)}
              </div>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-12 h-12 rounded-lg bg-danger/10 flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-danger" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Đã chi tiêu</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(totalSpent)}
              </div>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-12 h-12 rounded-lg bg-success/10 flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-success" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Còn lại</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(totalBudget - totalSpent)}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Budget List */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {loading ? (
          <div className="col-span-2 flex justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        ) : budgets.length === 0 ? (
          <div className="col-span-2 text-center py-12 card">
            <p className="text-gray-500">Chưa có ngân sách nào cho tháng này</p>
          </div>
        ) : (
          budgets.map((budget) => (
            <div key={budget.id} className="card">
              <div className="flex items-center justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 rounded-lg bg-gray-100 flex items-center justify-center">
                    <span className="text-xl">{budget.categoryIcon || '💰'}</span>
                  </div>
                  <div>
                    <h3 className="font-semibold text-gray-900">
                      {budget.categoryName}
                    </h3>
                    <p className="text-sm text-gray-500">
                      Tháng {budget.month}/{budget.year}
                    </p>
                  </div>
                </div>
                <span
                  className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(
                    budget.percentUsed
                  )}`}
                >
                  {budget.percentUsed}%
                </span>
              </div>

              <div className="space-y-2">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Đã chi</span>
                  <span className="font-medium text-gray-900">
                    {formatCurrency(budget.spent)}
                  </span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-2.5">
                  <div
                    className={`h-2.5 rounded-full transition-all ${getProgressColor(
                      budget.percentUsed
                    )}`}
                    style={{ width: `${Math.min(budget.percentUsed, 100)}%` }}
                  />
                </div>
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Ngân sách</span>
                  <span className="font-medium text-gray-900">
                    {formatCurrency(budget.amount)}
                  </span>
                </div>
              </div>

              {budget.percentUsed >= 80 && (
                <div
                  className={`mt-4 p-3 rounded-lg ${
                    budget.percentUsed >= 100
                      ? 'bg-danger/10 text-danger'
                      : 'bg-warning/10 text-warning'
                  }`}
                >
                  <p className="text-sm font-medium">
                    {budget.percentUsed >= 100
                      ? '⚠️ Đã vượt ngân sách!'
                      : '⚠️ Sắp vượt ngân sách'}
                  </p>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

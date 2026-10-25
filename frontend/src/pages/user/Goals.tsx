import { useEffect, useState } from 'react';
import { Plus, Target, TrendingUp } from 'lucide-react';
import { useSpaceStore } from '../../store/spaceStore';
import api from '../../lib/axios';
import { SavingsGoal } from '../../types';
import { formatCurrency, formatDate } from '../../lib/utils';
import toast from 'react-hot-toast';

export default function Goals() {
  const { currentSpace } = useSpaceStore();
  const [goals, setGoals] = useState<SavingsGoal[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (currentSpace) {
      fetchGoals();
    }
  }, [currentSpace]);

  const fetchGoals = async () => {
    if (!currentSpace) return;

    try {
      setLoading(true);
      const response = await api.get<SavingsGoal[]>(
        `/savings-goals?spaceId=${currentSpace.id}`
      );
      setGoals(response.data);
    } catch (error) {
      toast.error('Không thể tải danh sách mục tiêu');
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACHIEVED':
        return 'badge-success';
      case 'CANCELLED':
        return 'badge-danger';
      default:
        return 'badge-primary';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'ACHIEVED':
        return 'Đã đạt';
      case 'CANCELLED':
        return 'Đã hủy';
      default:
        return 'Đang thực hiện';
    }
  };

  if (!currentSpace) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-gray-500">Vui lòng chọn không gian tài chính</p>
      </div>
    );
  }

  const totalTarget = goals.reduce((sum, g) => sum + g.targetAmount, 0);
  const totalSaved = goals.reduce((sum, g) => sum + g.currentAmount, 0);
  const activeGoals = goals.filter((g) => g.status === 'IN_PROGRESS').length;

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Savings Goals</h1>
          <p className="text-gray-500 mt-1">Theo dõi và quản lý mục tiêu tiết kiệm</p>
        </div>
        <button className="btn btn-primary flex items-center gap-2">
          <Plus className="w-5 h-5" />
          Thêm mục tiêu
        </button>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-lg bg-primary/10 flex items-center justify-center">
              <Target className="w-6 h-6 text-primary" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Mục tiêu đang thực hiện</div>
              <div className="text-2xl font-bold text-gray-900">{activeGoals}</div>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-lg bg-success/10 flex items-center justify-center">
              <TrendingUp className="w-6 h-6 text-success" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Tổng đã tiết kiệm</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(totalSaved)}
              </div>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-12 h-12 rounded-lg bg-warning/10 flex items-center justify-center">
              <Target className="w-6 h-6 text-warning" />
            </div>
            <div>
              <div className="text-sm text-gray-600">Tổng mục tiêu</div>
              <div className="text-2xl font-bold text-gray-900">
                {formatCurrency(totalTarget)}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Goals List */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full flex justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        ) : goals.length === 0 ? (
          <div className="col-span-full text-center py-12 card">
            <Target className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">Chưa có mục tiêu tiết kiệm nào</p>
            <button className="btn btn-primary mt-4">Tạo mục tiêu đầu tiên</button>
          </div>
        ) : (
          goals.map((goal) => (
            <div key={goal.id} className="card hover:shadow-md transition-shadow">
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <h3 className="font-semibold text-lg text-gray-900 mb-1">
                    {goal.name}
                  </h3>
                  {goal.description && (
                    <p className="text-sm text-gray-500 line-clamp-2">
                      {goal.description}
                    </p>
                  )}
                </div>
                <span className={`badge ${getStatusColor(goal.status)}`}>
                  {getStatusText(goal.status)}
                </span>
              </div>

              <div className="space-y-3">
                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Đã tiết kiệm</span>
                  <span className="font-medium text-gray-900">
                    {formatCurrency(goal.currentAmount)}
                  </span>
                </div>

                <div className="relative">
                  <div className="w-full bg-gray-200 rounded-full h-3">
                    <div
                      className="bg-success h-3 rounded-full transition-all"
                      style={{ width: `${Math.min(goal.progress, 100)}%` }}
                    />
                  </div>
                  <span className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 text-xs font-medium text-gray-700">
                    {goal.progress}%
                  </span>
                </div>

                <div className="flex justify-between text-sm">
                  <span className="text-gray-600">Mục tiêu</span>
                  <span className="font-medium text-gray-900">
                    {formatCurrency(goal.targetAmount)}
                  </span>
                </div>

                <div className="pt-3 border-t border-gray-100">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-gray-600">Hạn chót</span>
                    <span className="font-medium text-gray-900">
                      {formatDate(goal.targetDate)}
                    </span>
                  </div>
                </div>
              </div>

              {goal.status === 'IN_PROGRESS' && (
                <div className="mt-4 pt-4 border-t border-gray-100 flex gap-2">
                  <button className="btn btn-primary flex-1">Thêm tiền</button>
                  <button className="btn btn-secondary">Chi tiết</button>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}

import { useEffect, useState } from 'react';
import { Plus, Filter, Download, Search } from 'lucide-react';
import { useSpaceStore } from '../../store/spaceStore';
import api from '../../lib/axios';
import { Transaction, PageResponse, Category } from '../../types';
import { formatCurrency, formatDate } from '../../lib/utils';
import toast from 'react-hot-toast';

export default function Transactions() {
  const { currentSpace } = useSpaceStore();
  const [transactions, setTransactions] = useState<PageResponse<Transaction> | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [filters, setFilters] = useState({
    type: '',
    categoryId: '',
    keyword: '',
    page: 0,
    size: 20,
  });

  useEffect(() => {
    if (currentSpace) {
      fetchTransactions();
      fetchCategories();
    }
  }, [currentSpace, filters]);

  const fetchTransactions = async () => {
    if (!currentSpace) return;

    try {
      setLoading(true);
      const params = new URLSearchParams({
        spaceId: currentSpace.id.toString(),
        page: filters.page.toString(),
        size: filters.size.toString(),
        ...(filters.type && { type: filters.type }),
        ...(filters.categoryId && { categoryId: filters.categoryId }),
        ...(filters.keyword && { keyword: filters.keyword }),
      });

      const response = await api.get<PageResponse<Transaction>>(
        `/transactions?${params.toString()}`
      );
      setTransactions(response.data);
    } catch (error) {
      toast.error('Không thể tải danh sách giao dịch');
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await api.get<Category[]>('/categories');
      setCategories(response.data);
    } catch (error) {
      console.error('Failed to fetch categories');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Bạn có chắc muốn xóa giao dịch này?')) return;

    try {
      await api.delete(`/transactions/${id}`);
      toast.success('Đã xóa giao dịch');
      fetchTransactions();
    } catch (error) {
      toast.error('Không thể xóa giao dịch');
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
          <h1 className="text-3xl font-bold text-gray-900">Transactions</h1>
          <p className="text-gray-500 mt-1">Quản lý tất cả các giao dịch thu chi</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="btn btn-primary flex items-center gap-2"
        >
          <Plus className="w-5 h-5" />
          Thêm giao dịch
        </button>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
            <input
              type="text"
              placeholder="Tìm kiếm..."
              value={filters.keyword}
              onChange={(e) => setFilters({ ...filters, keyword: e.target.value, page: 0 })}
              className="input pl-10"
            />
          </div>

          <select
            value={filters.type}
            onChange={(e) => setFilters({ ...filters, type: e.target.value, page: 0 })}
            className="input"
          >
            <option value="">Tất cả loại</option>
            <option value="INCOME">Thu nhập</option>
            <option value="EXPENSE">Chi tiêu</option>
          </select>

          <select
            value={filters.categoryId}
            onChange={(e) => setFilters({ ...filters, categoryId: e.target.value, page: 0 })}
            className="input"
          >
            <option value="">Tất cả danh mục</option>
            {categories.map((cat) => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>

          <button className="btn btn-secondary flex items-center gap-2">
            <Download className="w-4 h-4" />
            Xuất Excel
          </button>
        </div>
      </div>

      {/* Transactions Table */}
      <div className="card">
        {loading ? (
          <div className="flex justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-gray-200">
                    <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                      Giao dịch
                    </th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                      Ngày
                    </th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                      Danh mục
                    </th>
                    <th className="text-left py-3 px-4 text-sm font-medium text-gray-600">
                      Loại
                    </th>
                    <th className="text-right py-3 px-4 text-sm font-medium text-gray-600">
                      Số tiền
                    </th>
                    <th className="text-right py-3 px-4 text-sm font-medium text-gray-600">
                      Thao tác
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {transactions?.content.map((transaction) => (
                    <tr
                      key={transaction.id}
                      className="border-b border-gray-100 hover:bg-gray-50"
                    >
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-3">
                          <div
                            className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                              transaction.type === 'INCOME'
                                ? 'bg-success/10'
                                : 'bg-danger/10'
                            }`}
                          >
                            <span>{transaction.categoryIcon || '💰'}</span>
                          </div>
                          <div>
                            <div className="font-medium text-sm text-gray-900">
                              {transaction.description || transaction.categoryName}
                            </div>
                            <div className="text-xs text-gray-500">
                              {transaction.createdBy}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="py-3 px-4 text-sm text-gray-600">
                        {formatDate(transaction.transactionDate)}
                      </td>
                      <td className="py-3 px-4 text-sm text-gray-600">
                        {transaction.categoryName}
                      </td>
                      <td className="py-3 px-4">
                        <span
                          className={`badge ${
                            transaction.type === 'INCOME'
                              ? 'badge-success'
                              : 'badge-danger'
                          }`}
                        >
                          {transaction.type === 'INCOME' ? 'Thu nhập' : 'Chi tiêu'}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-right">
                        <span
                          className={`font-medium ${
                            transaction.type === 'INCOME'
                              ? 'text-success'
                              : 'text-danger'
                          }`}
                        >
                          {transaction.type === 'INCOME' ? '+' : '-'}
                          {formatCurrency(transaction.amount)}
                        </span>
                      </td>
                      <td className="py-3 px-4 text-right">
                        <button
                          onClick={() => handleDelete(transaction.id)}
                          className="text-danger hover:text-danger-dark text-sm"
                        >
                          Xóa
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {transactions && transactions.totalPages > 1 && (
              <div className="flex items-center justify-between mt-6 pt-6 border-t border-gray-200">
                <div className="text-sm text-gray-600">
                  Hiển thị {transactions.content.length} trên {transactions.totalElements}{' '}
                  giao dịch
                </div>
                <div className="flex gap-2">
                  <button
                    onClick={() =>
                      setFilters({ ...filters, page: Math.max(0, filters.page - 1) })
                    }
                    disabled={transactions.page === 0}
                    className="btn btn-secondary disabled:opacity-50"
                  >
                    Trước
                  </button>
                  <button
                    onClick={() =>
                      setFilters({
                        ...filters,
                        page: Math.min(transactions.totalPages - 1, filters.page + 1),
                      })
                    }
                    disabled={transactions.page >= transactions.totalPages - 1}
                    className="btn btn-secondary disabled:opacity-50"
                  >
                    Sau
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

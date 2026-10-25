export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'USER' | 'ADMIN';
  isActive: boolean;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
}

export interface FinancialSpace {
  id: number;
  name: string;
  type: 'PERSONAL' | 'FAMILY';
  description?: string;
  balance: number;
  memberCount?: number;
  isActive: boolean;
  createdAt: string;
}

export interface Category {
  id: number;
  name: string;
  type: 'INCOME' | 'EXPENSE';
  icon?: string;
  color?: string;
  isSystem: boolean;
  isActive: boolean;
}

export interface Transaction {
  id: number;
  spaceId: number;
  spaceName: string;
  categoryId: number;
  categoryName: string;
  categoryIcon?: string;
  type: 'INCOME' | 'EXPENSE';
  amount: number;
  description?: string;
  transactionDate: string;
  createdBy: string;
  createdAt: string;
}

export interface Budget {
  id: number;
  spaceId: number;
  categoryId: number;
  categoryName: string;
  categoryIcon?: string;
  amount: number;
  spent: number;
  month: number;
  year: number;
  percentUsed: number;
}

export interface SavingsGoal {
  id: number;
  spaceId: number;
  name: string;
  targetAmount: number;
  currentAmount: number;
  targetDate: string;
  status: 'IN_PROGRESS' | 'ACHIEVED' | 'CANCELLED';
  progress: number;
  description?: string;
}

export interface DashboardResponse {
  totalBalance: number;
  monthIncome: number;
  monthExpense: number;
  monthSavings: number;
  incomeChange: number;
  expenseChange: number;
  categoryExpenses: Array<{
    categoryName: string;
    categoryIcon?: string;
    amount: number;
    percentage: number;
  }>;
  dailyExpenses: Array<{
    date: string;
    amount: number;
  }>;
  recentTransactions: Transaction[];
  budgetAlerts: Array<{
    categoryName: string;
    spent: number;
    budget: number;
    percentUsed: number;
  }>;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface AdminDashboard {
  totalUsers: number;
  activeUsers: number;
  totalTransactions: number;
  totalRevenue: number;
  userGrowth: number;
  transactionGrowth: number;
  recentUsers: User[];
  usersByMonth: Array<{
    month: string;
    count: number;
  }>;
}

export interface AuditLog {
  id: number;
  adminId: number;
  adminName: string;
  action: string;
  entityType: string;
  entityId: number;
  details: string;
  createdAt: string;
}

export interface Notification {
  id: number;
  type: 'BUDGET_ALERT' | 'GOAL_ACHIEVED' | 'TRANSACTION' | 'SYSTEM';
  title: string;
  message: string;
  isRead: boolean;
  createdAt: string;
}

import { useState } from 'react';
import { User, Lock, Bell, Wallet, Save } from 'lucide-react';
import { useAuthStore } from '../../store/authStore';
import api from '../../lib/axios';
import toast from 'react-hot-toast';

export default function Settings() {
  const { user, updateUser } = useAuthStore();
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState<'profile' | 'password' | 'spaces'>('profile');

  const [profileData, setProfileData] = useState({
    fullName: user?.fullName || '',
    email: user?.email || '',
  });

  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const [profileErrors, setProfileErrors] = useState({
    fullName: '',
    email: '',
  });

  const [passwordErrors, setPasswordErrors] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const validateProfile = () => {
    const newErrors = {
      fullName: '',
      email: '',
    };

    if (!profileData.fullName.trim()) {
      newErrors.fullName = 'Vui lòng nhập họ tên';
    } else if (profileData.fullName.trim().length < 3) {
      newErrors.fullName = 'Họ tên phải có ít nhất 3 ký tự';
    }

    if (!profileData.email.trim()) {
      newErrors.email = 'Vui lòng nhập email';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profileData.email)) {
      newErrors.email = 'Email không hợp lệ';
    }

    setProfileErrors(newErrors);
    return !Object.values(newErrors).some(error => error !== '');
  };

  const validatePassword = () => {
    const newErrors = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    };

    if (!passwordData.currentPassword) {
      newErrors.currentPassword = 'Vui lòng nhập mật khẩu hiện tại';
    }

    if (!passwordData.newPassword) {
      newErrors.newPassword = 'Vui lòng nhập mật khẩu mới';
    } else if (passwordData.newPassword.length < 6) {
      newErrors.newPassword = 'Mật khẩu phải có ít nhất 6 ký tự';
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(passwordData.newPassword)) {
      newErrors.newPassword = 'Mật khẩu phải có chữ hoa, chữ thường và số';
    }

    if (!passwordData.confirmPassword) {
      newErrors.confirmPassword = 'Vui lòng xác nhận mật khẩu mới';
    } else if (passwordData.newPassword !== passwordData.confirmPassword) {
      newErrors.confirmPassword = 'Mật khẩu xác nhận không khớp';
    }

    setPasswordErrors(newErrors);
    return !Object.values(newErrors).some(error => error !== '');
  };

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateProfile()) {
      return;
    }

    setLoading(true);

    try {
      const response = await api.put('/users/profile', profileData);
      updateUser(response.data);
      toast.success('Cập nhật thông tin thành công');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Cập nhật thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validatePassword()) {
      return;
    }

    setLoading(true);
    try {
      await api.put('/auth/change-password', {
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword,
      });
      toast.success('Đổi mật khẩu thành công');
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      });
      setPasswordErrors({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      });
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Đổi mật khẩu thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleProfileChange = (field: string, value: string) => {
    setProfileData({ ...profileData, [field]: value });
    if (profileErrors[field as keyof typeof profileErrors]) {
      setProfileErrors({ ...profileErrors, [field]: '' });
    }
  };

  const handlePasswordChange = (field: string, value: string) => {
    setPasswordData({ ...passwordData, [field]: value });
    if (passwordErrors[field as keyof typeof passwordErrors]) {
      setPasswordErrors({ ...passwordErrors, [field]: '' });
    }
  };

  const tabs = [
    { id: 'profile', label: 'Thông tin cá nhân', icon: User },
    { id: 'password', label: 'Đổi mật khẩu', icon: Lock },
    { id: 'spaces', label: 'Không gian tài chính', icon: Wallet },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Settings</h1>
        <p className="text-gray-500 mt-1">Quản lý thông tin tài khoản và cài đặt</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Tabs */}
        <div className="card lg:col-span-1">
          <nav className="space-y-1">
            {tabs.map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg text-left transition-colors ${
                    activeTab === tab.id
                      ? 'bg-primary text-white'
                      : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                  <span className="font-medium">{tab.label}</span>
                </button>
              );
            })}
          </nav>
        </div>

        {/* Content */}
        <div className="card lg:col-span-3">
          {activeTab === 'profile' && (
            <form onSubmit={handleUpdateProfile} className="space-y-6">
              <h2 className="text-xl font-semibold text-gray-900">Thông tin cá nhân</h2>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Username
                </label>
                <input
                  type="text"
                  value={user?.username}
                  disabled
                  className="input bg-gray-100"
                />
                <p className="text-xs text-gray-500 mt-1">Username không thể thay đổi</p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Họ và tên <span className="text-danger">*</span>
                </label>
                <input
                  type="text"
                  value={profileData.fullName}
                  onChange={(e) => handleProfileChange('fullName', e.target.value)}
                  className={`input ${profileErrors.fullName ? 'border-danger focus:ring-danger' : ''}`}
                />
                {profileErrors.fullName && (
                  <p className="mt-1 text-sm text-danger">{profileErrors.fullName}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Email <span className="text-danger">*</span>
                </label>
                <input
                  type="email"
                  value={profileData.email}
                  onChange={(e) => handleProfileChange('email', e.target.value)}
                  className={`input ${profileErrors.email ? 'border-danger focus:ring-danger' : ''}`}
                />
                {profileErrors.email && (
                  <p className="mt-1 text-sm text-danger">{profileErrors.email}</p>
                )}
              </div>

              <button
                type="submit"
                disabled={loading}
                className="btn btn-primary flex items-center gap-2"
              >
                <Save className="w-4 h-4" />
                {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
              </button>
            </form>
          )}

          {activeTab === 'password' && (
            <form onSubmit={handleChangePassword} className="space-y-6">
              <h2 className="text-xl font-semibold text-gray-900">Đổi mật khẩu</h2>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Mật khẩu hiện tại <span className="text-danger">*</span>
                </label>
                <input
                  type="password"
                  value={passwordData.currentPassword}
                  onChange={(e) => handlePasswordChange('currentPassword', e.target.value)}
                  className={`input ${passwordErrors.currentPassword ? 'border-danger focus:ring-danger' : ''}`}
                />
                {passwordErrors.currentPassword && (
                  <p className="mt-1 text-sm text-danger">{passwordErrors.currentPassword}</p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Mật khẩu mới <span className="text-danger">*</span>
                </label>
                <input
                  type="password"
                  value={passwordData.newPassword}
                  onChange={(e) => handlePasswordChange('newPassword', e.target.value)}
                  className={`input ${passwordErrors.newPassword ? 'border-danger focus:ring-danger' : ''}`}
                />
                {passwordErrors.newPassword && (
                  <p className="mt-1 text-sm text-danger">{passwordErrors.newPassword}</p>
                )}
                {!passwordErrors.newPassword && passwordData.newPassword && (
                  <p className="mt-1 text-xs text-gray-500">
                    Phải có ít nhất 6 ký tự, bao gồm chữ hoa, chữ thường và số
                  </p>
                )}
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Xác nhận mật khẩu mới <span className="text-danger">*</span>
                </label>
                <input
                  type="password"
                  value={passwordData.confirmPassword}
                  onChange={(e) => handlePasswordChange('confirmPassword', e.target.value)}
                  className={`input ${passwordErrors.confirmPassword ? 'border-danger focus:ring-danger' : ''}`}
                />
                {passwordErrors.confirmPassword && (
                  <p className="mt-1 text-sm text-danger">{passwordErrors.confirmPassword}</p>
                )}
              </div>

              <button
                type="submit"
                disabled={loading}
                className="btn btn-primary flex items-center gap-2"
              >
                <Lock className="w-4 h-4" />
                {loading ? 'Đang cập nhật...' : 'Đổi mật khẩu'}
              </button>
            </form>
          )}

          {activeTab === 'spaces' && (
            <div className="space-y-6">
              <h2 className="text-xl font-semibold text-gray-900">
                Không gian tài chính
              </h2>
              <p className="text-gray-600">
                Quản lý các không gian tài chính của bạn (Coming soon)
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

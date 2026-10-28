import { getErrorMessage } from '../../lib/apiError';
// Tác giả: Nguyễn Hoàng Phúc (Frontend Lead & UI/UX)
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { Wallet, Eye, EyeOff } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '../../lib/axios';

export default function Register() {
  const navigate = useNavigate();
  const { t } = useTranslation();
  const errorText = (key: string) => (key.startsWith('auth.') ? t(key) : key);
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    fullName: '',
  });
  const [errors, setErrors] = useState({
    username: '',
    email: '',
    password: '',
    fullName: '',
  });

  const validateForm = () => {
    const newErrors = {
      username: '',
      email: '',
      password: '',
      fullName: '',
    };

    if (!formData.fullName.trim()) {
      newErrors.fullName = 'auth.errors.fullNameRequired';
    } else if (formData.fullName.trim().length < 3) {
      newErrors.fullName = 'auth.errors.fullNameMin';
    }

    if (!formData.username.trim()) {
      newErrors.username = 'auth.errors.usernameRequired';
    } else if (formData.username.length < 3) {
      newErrors.username = 'auth.errors.usernameMin';
    } else if (!/^[a-zA-Z0-9._-]+$/.test(formData.username)) {
      newErrors.username = 'auth.errors.usernamePattern';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'auth.errors.emailRequired';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'auth.errors.emailInvalid';
    }

    if (!formData.password) {
      newErrors.password = 'auth.errors.passwordRequired';
    } else if (formData.password.length < 8) {
      newErrors.password = 'auth.errors.passwordMin';
    } else if (formData.password.length > 100) {
      newErrors.password = 'auth.errors.passwordMax';
    } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(formData.password)) {
      newErrors.password = 'auth.errors.passwordPattern';
    }

    setErrors(newErrors);
    return !Object.values(newErrors).some(error => error !== '');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      await api.post('/auth/register', formData);
      toast.success(t('auth.register.success'));
      navigate('/login');
    } catch (error) {
      const response = (
        error as {
          response?: { status?: number; data?: { fieldErrors?: Record<string, string> } };
        }
      ).response;
      const fieldErrors = response?.data?.fieldErrors;
      if (fieldErrors) {
        const fieldKeys: Record<string, string> = {
          username: 'auth.errors.usernameInvalid',
          email: 'auth.errors.emailInvalid',
          password: 'auth.errors.passwordInvalid',
          fullName: 'auth.errors.fullNameInvalid',
        };
        const mapped: Partial<typeof errors> = {};
        (['username', 'email', 'password', 'fullName'] as const).forEach((field) => {
          if (fieldErrors[field]) {
            mapped[field] = fieldKeys[field];
          }
        });
        if (Object.keys(mapped).length > 0) {
          setErrors((prev) => ({ ...prev, ...mapped }));
        }
        const firstKey = Object.keys(fieldErrors)[0];
        toast.error(
          firstKey && fieldKeys[firstKey] ? t(fieldKeys[firstKey]) : t('auth.register.failed')
        );
      } else if (response?.status === 409) {
        toast.error(t('auth.errors.duplicateUser'));
      } else {
        toast.error(getErrorMessage(error, t('auth.register.failed')));
      }
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: string, value: string) => {
    setFormData({ ...formData, [field]: value });
    // Clear error when user starts typing
    if (errors[field as keyof typeof errors]) {
      setErrors({ ...errors, [field]: '' });
    }
  };

  return (
    <div className="w-full max-w-md">
      <div className="bg-white rounded-2xl shadow-xl p-8">
        {/* Logo */}
        <div className="flex justify-center mb-8">
          <div className="flex items-center gap-2">
            <div className="w-12 h-12 bg-primary rounded-xl flex items-center justify-center">
              <Wallet className="w-7 h-7 text-white" />
            </div>
            <span className="text-2xl font-bold text-gray-900">Budgivee</span>
          </div>
        </div>

        <h1 className="text-2xl font-bold text-center mb-2">{t('auth.register.title')}</h1>
        <p className="text-gray-600 text-center mb-8">
          {t('auth.register.subtitle')}
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              {t('auth.register.fullName')} <span className="text-danger">*</span>
            </label>
            <input
              type="text"
              value={formData.fullName}
              onChange={(e) => handleChange('fullName', e.target.value)}
              className={`input ${errors.fullName ? 'border-danger focus:ring-danger' : ''}`}
              placeholder={t('auth.register.fullNamePlaceholder')}
            />
            {errors.fullName && (
              <p className="mt-1 text-sm text-danger">{errorText(errors.fullName)}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              {t('auth.register.username')} <span className="text-danger">*</span>
            </label>
            <input
              type="text"
              value={formData.username}
              onChange={(e) => handleChange('username', e.target.value)}
              className={`input ${errors.username ? 'border-danger focus:ring-danger' : ''}`}
              placeholder={t('auth.register.usernamePlaceholder')}
            />
            {errors.username && (
              <p className="mt-1 text-sm text-danger">{errorText(errors.username)}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              {t('auth.register.email')} <span className="text-danger">*</span>
            </label>
            <input
              type="email"
              value={formData.email}
              onChange={(e) => handleChange('email', e.target.value)}
              className={`input ${errors.email ? 'border-danger focus:ring-danger' : ''}`}
              placeholder={t('auth.register.emailPlaceholder')}
            />
            {errors.email && (
              <p className="mt-1 text-sm text-danger">{errorText(errors.email)}</p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              {t('auth.register.password')} <span className="text-danger">*</span>
            </label>
            <div className="relative">
              <input
                type={showPassword ? 'text' : 'password'}
                value={formData.password}
                onChange={(e) => handleChange('password', e.target.value)}
                className={`input pr-10 ${errors.password ? 'border-danger focus:ring-danger' : ''}`}
                placeholder={t('auth.register.passwordPlaceholder')}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                {showPassword ? (
                  <EyeOff className="w-5 h-5" />
                ) : (
                  <Eye className="w-5 h-5" />
                )}
              </button>
            </div>
            {errors.password && (
              <p className="mt-1 text-sm text-danger">{errorText(errors.password)}</p>
            )}
            {!errors.password && formData.password && (
              <p className="mt-1 text-xs text-gray-500">
                {t('auth.register.passwordHint')}
              </p>
            )}
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary w-full"
          >
            {loading ? t('auth.register.submitting') : t('auth.register.submit')}
          </button>
        </form>

        <div className="mt-6 text-center">
          <p className="text-gray-600">
            {t('auth.register.haveAccount')}{' '}
            <Link to="/login" className="text-primary hover:text-primary-dark font-medium">
              {t('auth.register.signIn')}
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}

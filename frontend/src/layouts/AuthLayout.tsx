// Tác giả: Nguyễn Hoàng Phúc (Frontend Lead & UI/UX)
import { Outlet } from 'react-router-dom';
import LanguageToggle from '../i18n/LanguageToggle';

export default function AuthLayout() {
  return (
    <div className="relative min-h-screen bg-gradient-to-br from-primary/10 via-success/5 to-primary/5 flex items-center justify-center p-4">
      <div className="absolute top-4 right-4">
        <LanguageToggle />
      </div>
      <Outlet />
    </div>
  );
}

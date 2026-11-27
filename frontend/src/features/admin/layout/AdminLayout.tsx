import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import {
  ShieldCheck,
  LayoutDashboard,
  Users,
  Building,
  BookOpen,
  Settings,
  Activity,
  LogOut,
  Bell,
  Search,
  Key
} from 'lucide-react';

export const AdminLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const navItems = [
    { name: 'Governance Cockpit', path: '/admin', icon: LayoutDashboard, end: true },
    { name: 'User Directory', path: '/admin/users', icon: Users },
    { name: 'Academic Departments', path: '/admin/departments', icon: Building },
    { name: 'Course Catalog Governance', path: '/admin/courses', icon: BookOpen },
    { name: 'Roles & Security Policies', path: '/admin/security', icon: Key },
    { name: 'System Telemetry & Audit', path: '/admin/telemetry', icon: Activity },
    { name: 'Platform Settings', path: '/admin/settings', icon: Settings },
  ];

  return (
    <div className="min-h-screen bg-slate-50 flex">
      {/* Sidebar Navigation */}
      <aside className="w-64 bg-slate-950 text-slate-300 border-r border-slate-800 flex flex-col fixed inset-y-0 z-30">
        {/* Brand */}
        <div className="h-16 px-6 flex items-center border-b border-slate-800 space-x-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-purple-600 to-indigo-600 flex items-center justify-center text-white shadow-md shadow-purple-600/30">
            <ShieldCheck className="w-5 h-5" />
          </div>
          <div>
            <span className="font-bold text-white text-base tracking-tight">Opt-Out LMS</span>
            <span className="block text-[10px] font-semibold uppercase tracking-wider text-purple-400">Institutional Admin</span>
          </div>
        </div>

        {/* Navigation Links */}
        <div className="flex-1 px-3 py-6 space-y-1 overflow-y-auto">
          {navItems.map((item) => (
            <NavLink
              key={item.name}
              to={item.path}
              end={item.end}
              className={({ isActive }) =>
                `flex items-center justify-between px-3.5 py-2.5 rounded-xl font-medium text-sm transition-all duration-150 ${
                  isActive
                    ? 'bg-purple-600 text-white font-semibold shadow-md shadow-purple-600/30'
                    : 'text-slate-400 hover:bg-slate-900 hover:text-white'
                }`
              }
            >
              {({ isActive }) => (
                <div className="flex items-center space-x-3">
                  <item.icon className={`w-4 h-4 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                  <span>{item.name}</span>
                </div>
              )}
            </NavLink>
          ))}
        </div>

        {/* Database Status Pill */}
        <div className="px-4 py-3">
          <div className="px-3 py-2 bg-slate-900 border border-slate-800 rounded-xl flex items-center justify-between text-[11px]">
            <span className="flex items-center text-emerald-400 font-semibold">
              <span className="w-2 h-2 rounded-full bg-emerald-500 mr-2 animate-pulse"></span>
              MySQL 8.4 LTS
            </span>
            <span className="text-slate-500 font-mono">99.98%</span>
          </div>
        </div>

        {/* User Card & Sign Out */}
        <div className="p-4 border-t border-slate-800">
          <div className="bg-slate-900 rounded-xl p-3 flex items-center justify-between">
            <div className="flex items-center space-x-3 min-w-0">
              <div className="w-9 h-9 rounded-full bg-purple-600 text-white flex items-center justify-center font-bold text-sm shrink-0">
                {user?.firstName?.[0] || 'A'}
              </div>
              <div className="min-w-0">
                <p className="text-sm font-semibold text-white truncate">{user?.fullName || 'Super Administrator'}</p>
                <p className="text-xs text-slate-400 truncate">{user?.email}</p>
              </div>
            </div>
            <button
              onClick={handleLogout}
              title="Sign Out"
              className="p-1.5 text-slate-400 hover:text-red-400 hover:bg-slate-800 rounded-lg transition-colors"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="pl-64 flex-1 flex flex-col min-h-screen">
        {/* Top Header Bar */}
        <header className="h-16 bg-white/90 backdrop-blur-md border-b border-slate-200 sticky top-0 z-20 px-8 flex items-center justify-between">
          <div className="relative w-80">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search users, courses, departments, or audit logs..."
              className="w-full pl-10 pr-4 py-1.5 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500/20 focus:border-purple-500 transition-all placeholder:text-slate-400"
            />
          </div>

          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2 text-xs font-semibold px-3 py-1.5 bg-purple-50 text-purple-700 rounded-full border border-purple-200/60">
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>Full System Authority</span>
            </div>

            <button className="relative p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors">
              <Bell className="w-4 h-4" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-purple-600 rounded-full"></span>
            </button>
          </div>
        </header>

        {/* Nested View Content */}
        <main className="flex-1 p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

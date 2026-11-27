import React from 'react';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import {
  LayoutDashboard,
  BookOpen,
  FileText,
  CheckSquare,
  Users,
  BarChart2,
  Sparkles,
  LogOut,
  Bell,
  Search,
  PlusCircle,
  Briefcase
} from 'lucide-react';

export const InstructorLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const navItems = [
    { name: 'Faculty Dashboard', path: '/instructor', icon: LayoutDashboard, end: true },
    { name: 'My Assigned Courses', path: '/instructor/courses', icon: BookOpen },
    { name: 'Assignment Grading Queue', path: '/instructor/submissions', icon: CheckSquare, badge: 'Active' },
    { name: 'Curriculum & Lessons', path: '/instructor/curriculum', icon: FileText },
    { name: 'Student Roster & Attendance', path: '/instructor/students', icon: Users },
    { name: 'Class Analytics', path: '/instructor/analytics', icon: BarChart2 },
    { name: 'AI Question Generator', path: '/instructor/ai-tools', icon: Sparkles, badge: 'Gemini Pro' },
  ];

  return (
    <div className="min-h-screen bg-slate-50 flex">
      {/* Sidebar Navigation */}
      <aside className="w-64 bg-slate-900 text-slate-300 border-r border-slate-800 flex flex-col fixed inset-y-0 z-30">
        {/* Brand */}
        <div className="h-16 px-6 flex items-center border-b border-slate-800 space-x-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-blue-500 to-indigo-500 flex items-center justify-center text-white shadow-md shadow-blue-500/20">
            <Briefcase className="w-5 h-5" />
          </div>
          <div>
            <span className="font-bold text-white text-base tracking-tight">Opt-Out LMS</span>
            <span className="block text-[10px] font-semibold uppercase tracking-wider text-blue-400">Faculty Workspace</span>
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
                    ? 'bg-blue-600 text-white font-semibold shadow-md shadow-blue-600/30'
                    : 'text-slate-400 hover:bg-slate-800 hover:text-white'
                }`
              }
            >
              {({ isActive }) => (
                <>
                  <div className="flex items-center space-x-3">
                    <item.icon className={`w-4 h-4 ${isActive ? 'text-white' : 'text-slate-400'}`} />
                    <span>{item.name}</span>
                  </div>
                  {item.badge && (
                    <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full shadow-sm ${
                      item.badge === 'Active' ? 'bg-amber-500/20 text-amber-300' : 'bg-gradient-to-r from-blue-500 to-indigo-500 text-white'
                    }`}>
                      {item.badge}
                    </span>
                  )}
                </>
              )}
            </NavLink>
          ))}
        </div>

        {/* Quick Action Button */}
        <div className="px-4 py-3">
          <button className="w-full py-2.5 px-3 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 text-white rounded-xl text-xs font-semibold shadow-md shadow-blue-900/50 transition-all flex items-center justify-center space-x-2">
            <PlusCircle className="w-4 h-4" />
            <span>Create New Course</span>
          </button>
        </div>

        {/* User Card & Sign Out */}
        <div className="p-4 border-t border-slate-800">
          <div className="bg-slate-800/80 rounded-xl p-3 flex items-center justify-between">
            <div className="flex items-center space-x-3 min-w-0">
              <div className="w-9 h-9 rounded-full bg-blue-500 text-white flex items-center justify-center font-bold text-sm shrink-0">
                {user?.firstName?.[0] || 'F'}
              </div>
              <div className="min-w-0">
                <p className="text-sm font-semibold text-white truncate">{user?.fullName || 'Faculty Member'}</p>
                <p className="text-xs text-slate-400 truncate">{user?.email}</p>
              </div>
            </div>
            <button
              onClick={handleLogout}
              title="Sign Out"
              className="p-1.5 text-slate-400 hover:text-red-400 hover:bg-slate-700 rounded-lg transition-colors"
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
              placeholder="Search courses, student IDs, or submissions..."
              className="w-full pl-10 pr-4 py-1.5 text-sm bg-slate-50 border border-slate-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500/20 focus:border-blue-500 transition-all placeholder:text-slate-400"
            />
          </div>

          <div className="flex items-center space-x-4">
            <div className="flex items-center space-x-2 text-xs font-semibold px-3 py-1.5 bg-blue-50 text-blue-700 rounded-full border border-blue-200/60">
              <span className="w-2 h-2 rounded-full bg-blue-600 animate-pulse"></span>
              <span>Faculty Portal Session</span>
            </div>

            <button className="relative p-2 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors">
              <Bell className="w-4 h-4" />
              <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-blue-600 rounded-full"></span>
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

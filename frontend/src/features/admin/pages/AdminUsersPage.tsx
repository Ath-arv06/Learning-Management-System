import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { RecentUser } from '../types';
import { CheckCircle } from 'lucide-react';

export const AdminUsersPage: React.FC = () => {
  const [users, setUsers] = useState<RecentUser[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient.get<RecentUser[]>('/admin/users')
      .then((res) => {
        setUsers(res.data || []);
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="p-8 text-center text-slate-500">Querying user directory...</div>;
  }

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">User Directory & Identity Governance</h1>
          <p className="text-sm text-slate-500">Manage student accounts, faculty credentials, and role privileges.</p>
        </div>
      </div>

      <div className="bg-white border border-slate-200 rounded-2xl overflow-hidden shadow-sm">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-50 border-b border-slate-200 text-xs font-bold text-slate-500 uppercase tracking-wider">
            <tr>
              <th className="px-6 py-4">Full Name & Email</th>
              <th className="px-6 py-4">Primary Role</th>
              <th className="px-6 py-4">Department</th>
              <th className="px-6 py-4">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {users.map((u) => (
              <tr key={u.id} className="hover:bg-slate-50/70 transition-colors">
                <td className="px-6 py-4">
                  <div className="font-semibold text-slate-900">{u.fullName}</div>
                  <div className="text-xs text-slate-400">{u.email}</div>
                </td>
                <td className="px-6 py-4">
                  <span className={`px-2.5 py-1 rounded-full text-xs font-bold ${
                    u.primaryRole === 'ROLE_INSTRUCTOR'
                      ? 'bg-blue-50 text-blue-700'
                      : u.primaryRole === 'ROLE_ADMIN'
                      ? 'bg-purple-50 text-purple-700'
                      : 'bg-emerald-50 text-emerald-700'
                  }`}>
                    {u.primaryRole.replace('ROLE_', '')}
                  </span>
                </td>
                <td className="px-6 py-4 text-slate-600">{u.departmentName}</td>
                <td className="px-6 py-4">
                  <span className="inline-flex items-center text-xs font-semibold text-emerald-600">
                    <CheckCircle className="w-3.5 h-3.5 mr-1" /> Active
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

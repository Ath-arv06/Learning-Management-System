import React, { useEffect, useState } from 'react';
import { apiClient } from '../../../lib/api-client';
import { Certificate } from '../types';
import {
  Award,
  ShieldCheck,
  Printer,
  Search,
  CheckCircle2,
  XCircle,
  GraduationCap,
  Calendar,
  Hash
} from 'lucide-react';

export const CertificateViewerPage: React.FC = () => {
  const [certificates, setCertificates] = useState<Certificate[]>([]);
  const [selectedCert, setSelectedCert] = useState<Certificate | null>(null);
  const [verifyUuid, setVerifyUuid] = useState('');
  const [verifyResult, setVerifyResult] = useState<Certificate | null>(null);
  const [verifyError, setVerifyError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get<Certificate[]>('/certificates/my-certificates')
      .then((res) => {
        setCertificates(res.data);
        if (res.data.length > 0) {
          setSelectedCert(res.data[0]);
        }
      })
      .catch((err) => console.error('Failed to load certificates:', err))
      .finally(() => setLoading(false));
  }, []);

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!verifyUuid.trim()) return;
    setVerifyError(null);
    setVerifyResult(null);

    try {
      const res = await apiClient.get<Certificate>(`/certificates/verify/${encodeURIComponent(verifyUuid.trim())}`);
      setVerifyResult(res.data);
    } catch {
      setVerifyError('No active certificate found matching that UUID.');
    }
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Academic Certificates & Verification</h1>
          <p className="text-sm text-slate-500 mt-1">
            Cryptographically signed completion credentials issued by Opt-Out University.
          </p>
        </div>
        {selectedCert && (
          <button
            onClick={handlePrint}
            className="inline-flex items-center px-4 py-2 bg-indigo-600 text-white text-sm font-semibold rounded-xl hover:bg-indigo-700 transition shadow-sm"
          >
            <Printer className="w-4 h-4 mr-2" />
            Print / Save PDF
          </button>
        )}
      </div>

      {/* Public Verification Bar */}
      <div className="bg-white rounded-2xl border border-slate-200 p-5 shadow-sm">
        <form onSubmit={handleVerify} className="flex flex-col sm:flex-row gap-3">
          <div className="relative flex-1">
            <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={verifyUuid}
              onChange={(e) => setVerifyUuid(e.target.value)}
              placeholder="Verify any certificate UUID (e.g. CERT-2026-CS101-8F329A)"
              className="w-full pl-10 pr-4 py-2.5 text-sm border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <button
            type="submit"
            className="px-5 py-2.5 bg-slate-900 text-white text-sm font-semibold rounded-xl hover:bg-slate-800 transition"
          >
            Verify Credential
          </button>
        </form>

        {verifyResult && (
          <div className="mt-4 p-4 rounded-xl bg-emerald-50 border border-emerald-200 flex items-start space-x-3">
            <CheckCircle2 className="w-5 h-5 text-emerald-600 shrink-0 mt-0.5" />
            <div className="text-sm">
              <p className="font-bold text-emerald-900">
                Authentic Credential Verified — {verifyResult.certificateUuid}
              </p>
              <p className="text-emerald-700 mt-0.5">
                Awarded to <span className="font-semibold">{verifyResult.studentName}</span> for{' '}
                <span className="font-semibold">
                  {verifyResult.courseCode}: {verifyResult.courseTitle}
                </span>{' '}
                (Grade: {verifyResult.finalGrade}%).
              </p>
            </div>
          </div>
        )}

        {verifyError && (
          <div className="mt-4 p-4 rounded-xl bg-rose-50 border border-rose-200 flex items-center space-x-3 text-sm text-rose-700">
            <XCircle className="w-5 h-5 text-rose-600 shrink-0" />
            <span>{verifyError}</span>
          </div>
        )}
      </div>

      {/* Main Content */}
      {loading ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center text-sm text-slate-500">
          Loading your academic credentials...
        </div>
      ) : certificates.length === 0 ? (
        <div className="bg-white rounded-2xl border border-slate-200 p-12 text-center">
          <Award className="w-12 h-12 text-slate-300 mx-auto mb-3" />
          <h3 className="text-base font-bold text-slate-800">No Certificates Issued Yet</h3>
          <p className="text-sm text-slate-500 mt-1">
            Complete a course and pass its final assessment to earn a verified university certificate.
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Earned List */}
          <div className="space-y-3">
            <h2 className="text-xs font-bold uppercase tracking-wider text-slate-400 px-1">
              Earned Credentials ({certificates.length})
            </h2>
            {certificates.map((cert) => (
              <button
                key={cert.id}
                onClick={() => setSelectedCert(cert)}
                className={`w-full text-left p-4 rounded-2xl border transition ${
                  selectedCert?.id === cert.id
                    ? 'bg-indigo-50 border-indigo-300 shadow-sm'
                    : 'bg-white border-slate-200 hover:border-indigo-200'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-indigo-600">{cert.courseCode}</span>
                  <span className="inline-flex items-center text-[11px] font-semibold px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-700">
                    {cert.status}
                  </span>
                </div>
                <p className="font-bold text-slate-900 mt-1 text-sm">{cert.courseTitle}</p>
                <p className="text-xs text-slate-500 mt-1">
                  Issued {new Date(cert.issueDate).toLocaleDateString()} • Grade {cert.finalGrade}%
                </p>
              </button>
            ))}
          </div>

          {/* Official Certificate Preview */}
          {selectedCert && (
            <div className="lg:col-span-2 bg-white rounded-2xl border-2 border-slate-200 p-8 sm:p-10 shadow-sm relative overflow-hidden">
              <div className="border-4 border-double border-indigo-100 rounded-xl p-6 sm:p-8 text-center">
                <div className="w-14 h-14 rounded-2xl bg-indigo-600 text-white flex items-center justify-center mx-auto mb-4 shadow-md">
                  <GraduationCap className="w-8 h-8" />
                </div>
                <p className="text-xs font-bold uppercase tracking-[0.25em] text-indigo-600">
                  Opt-Out University
                </p>
                <h2 className="text-2xl sm:text-3xl font-serif font-bold text-slate-900 mt-2">
                  Certificate of Academic Completion
                </h2>
                <p className="text-sm text-slate-500 mt-4">This is to certify that</p>
                <p className="text-2xl font-bold text-slate-900 mt-2">{selectedCert.studentName}</p>
                <p className="text-sm text-slate-500 mt-3">has successfully completed all requirements for</p>
                <p className="text-xl font-bold text-indigo-700 mt-2">
                  {selectedCert.courseCode} — {selectedCert.courseTitle}
                </p>
                <p className="text-sm text-slate-600 mt-2">
                  with a final academic standing of <span className="font-bold">{selectedCert.finalGrade}%</span>
                </p>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mt-8 pt-6 border-t border-slate-200 text-left">
                  <div>
                    <p className="text-[11px] uppercase tracking-wider text-slate-400 font-semibold">
                      Authorized Faculty Issuer
                    </p>
                    <p className="text-sm font-bold text-slate-800 mt-1">{selectedCert.issuerName}</p>
                    <div className="flex items-center text-xs text-slate-500 mt-1">
                      <Calendar className="w-3.5 h-3.5 mr-1.5" />
                      {new Date(selectedCert.issueDate).toLocaleDateString()}
                    </div>
                  </div>
                  <div className="bg-slate-50 rounded-xl p-3 border border-slate-200">
                    <div className="flex items-center text-xs font-bold text-emerald-700">
                      <ShieldCheck className="w-4 h-4 mr-1.5" />
                      Cryptographically Verified
                    </div>
                    <p className="text-[11px] font-mono text-slate-600 mt-1 truncate">
                      ID: {selectedCert.certificateUuid}
                    </p>
                    <div className="flex items-center text-[10px] font-mono text-slate-400 mt-0.5 truncate">
                      <Hash className="w-3 h-3 mr-1 shrink-0" />
                      <span className="truncate">{selectedCert.verificationHash}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

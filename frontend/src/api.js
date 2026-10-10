// API Bridge with Spring Boot backend & fallback simulator

export const api = {
  backendOnline: false,
  lastPing: null,

  async checkHealth() {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 2000);
      const res = await fetch('/api/v1/auth/health', { signal: controller.signal });
      clearTimeout(timeoutId);
      this.backendOnline = res.ok;
      this.lastPing = new Date();
      return this.backendOnline;
    } catch {
      // Also try actuator health
      try {
        const controller2 = new AbortController();
        const timeoutId2 = setTimeout(() => controller2.abort(), 1500);
        const res2 = await fetch('/actuator/health', { signal: controller2.signal });
        clearTimeout(timeoutId2);
        this.backendOnline = res2.ok;
        this.lastPing = new Date();
        return this.backendOnline;
      } catch {
        this.backendOnline = false;
        this.lastPing = new Date();
        return false;
      }
    }
  },

  async login(email, password) {
    if (this.backendOnline) {
      try {
        const res = await fetch('/api/v1/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, password })
        });
        if (res.ok) {
          return await res.json();
        }
      } catch (err) {
        console.warn('Backend login fallback to mock:', err);
      }
    }
    // Simulated success
    return {
      token: "mock-jwt-session-" + Date.now(),
      email,
      role: email.includes("prof") ? "INSTRUCTOR" : email.includes("admin") ? "ADMIN" : "STUDENT"
    };
  },

  async getCourses() {
    if (this.backendOnline) {
      try {
        const res = await fetch('/api/v1/courses');
        if (res.ok) return await res.json();
      } catch (err) {
        console.warn('Using local course registry:', err);
      }
    }
    return null;
  }
};

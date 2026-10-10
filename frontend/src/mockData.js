// Comprehensive mock data aligning with backend entities

export const initialData = {
  currentUser: {
    id: "usr-01",
    email: "student@optout.edu",
    name: "Aiden Vance",
    role: "STUDENT",
    avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=256",
    department: "Computer Science",
    term: "Fall 2026",
    gpa: "3.84",
    creditsCompleted: 48,
    attendanceRate: 96
  },
  
  availableUsers: [
    {
      id: "usr-01",
      email: "student@optout.edu",
      name: "Aiden Vance",
      role: "STUDENT",
      avatar: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&q=80&w=256",
      department: "Computer Science & Engineering",
      term: "Fall 2026",
      status: "ACTIVE"
    },
    {
      id: "usr-02",
      email: "prof.elena@optout.edu",
      name: "Dr. Elena Rostova",
      role: "INSTRUCTOR",
      avatar: "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&q=80&w=256",
      department: "Distributed Systems & Cloud",
      term: "Faculty Lead",
      status: "ACTIVE"
    },
    {
      id: "usr-03",
      email: "admin@optout.edu",
      name: "System Administrator",
      role: "ADMIN",
      avatar: "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=256",
      department: "Academic Operations & IT",
      term: "Central Governance",
      status: "ACTIVE"
    }
  ],

  courses: [
    {
      id: "crs-101",
      code: "CS-482",
      title: "Advanced Cloud Architecture & Microservices",
      instructor: "Dr. Elena Rostova",
      department: "Computer Science",
      image: "/courses/cloud-arch.jpg",
      progress: 68,
      status: "IN_PROGRESS",
      description: "Master distributed systems, Kubernetes orchestration, resilient microservice communication patterns, and cloud-native observability.",
      credits: 4,
      totalHours: 45,
      completedHours: 30,
      enrolledCount: 142,
      rating: 4.9,
      modules: [
        {
          id: "mod-1",
          title: "Module 1: Domain-Driven Microservices Design",
          completed: true,
          lessons: [
            { id: "les-1", title: "1.1 Bounded Contexts & Aggregate Roots", type: "video", duration: "24 min", completed: true, videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4" },
            { id: "les-2", title: "1.2 Synchronous REST vs Asynchronous Event Buses", type: "reading", duration: "18 min", completed: true, notes: "Eventual consistency trade-offs, outbox pattern, and transactional messaging with Apache Kafka." },
            { id: "les-3", title: "1.3 Database Per Service Patterns", type: "quiz", duration: "15 min", completed: true, score: "95%" }
          ]
        },
        {
          id: "mod-2",
          title: "Module 2: Cloud Native Resilience & Fault Tolerance",
          completed: false,
          lessons: [
            { id: "les-4", title: "2.1 Circuit Breakers with Resilience4j & Envoy", type: "video", duration: "32 min", completed: true, videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4" },
            { id: "les-5", title: "2.2 Distributed Tracing & OpenTelemetry", type: "video", duration: "28 min", completed: false, videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4" },
            { id: "les-6", title: "2.3 Hands-on Lab: Chaos Engineering Injection", type: "assignment", duration: "45 min", completed: false }
          ]
        },
        {
          id: "mod-3",
          title: "Module 3: Zero-Trust Security & Identity Gateways",
          completed: false,
          lessons: [
            { id: "les-7", title: "3.1 OAuth2 / OIDC & JWT Token Validation", type: "video", duration: "35 min", completed: false, videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4" },
            { id: "les-8", title: "3.2 Service Mesh mTLS & Spiffe/Spire", type: "reading", duration: "20 min", completed: false }
          ]
        }
      ]
    },
    {
      id: "crs-102",
      code: "AI-501",
      title: "Neural Networks & Deep Learning Architectures",
      instructor: "Prof. Marcus Thorne",
      department: "Artificial Intelligence",
      image: "/courses/ai-systems.jpg",
      progress: 42,
      status: "IN_PROGRESS",
      description: "Explore transformer mechanics, diffusion generative models, multi-modal embeddings, and high-performance tensor computing.",
      credits: 4,
      totalHours: 50,
      completedHours: 21,
      enrolledCount: 198,
      rating: 4.95,
      modules: [
        {
          id: "mod-201",
          title: "Module 1: Mathematical Foundations & Gradient Dynamics",
          completed: true,
          lessons: [
            { id: "les-201", title: "1.1 Backpropagation & Jacobian Matrices", type: "video", duration: "40 min", completed: true, videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4" },
            { id: "les-202", title: "1.2 AdamW & Learning Rate Schedulers", type: "reading", duration: "25 min", completed: true }
          ]
        },
        {
          id: "mod-202",
          title: "Module 2: Self-Attention & Transformer Blocks",
          completed: false,
          lessons: [
            { id: "les-203", title: "2.1 Scaled Dot-Product Attention Mechanics", type: "video", duration: "48 min", completed: true, videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4" },
            { id: "les-204", title: "2.2 FlashAttention Optimization & Memory Boundness", type: "video", duration: "36 min", completed: false, videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4" }
          ]
        }
      ]
    },
    {
      id: "crs-103",
      code: "SEC-410",
      title: "Enterprise Cybersecurity & Zero-Trust Defense",
      instructor: "Dr. Alistair Chen",
      department: "Cybersecurity",
      image: "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?auto=format&fit=crop&q=80&w=800",
      progress: 90,
      status: "IN_PROGRESS",
      description: "Threat modeling, cryptographic protocols, defensive posture orchestration, and automated vulnerability telemetry.",
      credits: 3,
      totalHours: 36,
      completedHours: 32,
      enrolledCount: 88,
      rating: 4.85,
      modules: [
        {
          id: "mod-301",
          title: "Module 1: Cryptographic Primitives & Key Management",
          completed: true,
          lessons: [
            { id: "les-301", title: "1.1 Post-Quantum Cryptography Candidates", type: "reading", duration: "30 min", completed: true },
            { id: "les-302", title: "1.2 HSM & Automated Certificate Renewal", type: "video", duration: "25 min", completed: true, videoUrl: "https://www.w3schools.com/html/mov_bbb.mp4" }
          ]
        }
      ]
    }
  ],

  assignments: [
    {
      id: "asg-01",
      courseId: "crs-101",
      courseName: "CS-482 Cloud Architecture",
      title: "Resilient Microservices Topology & Gateway Lab",
      dueDate: "Tomorrow, 11:59 PM",
      status: "PENDING",
      points: 100,
      submissionsCount: 114,
      totalStudents: 142,
      description: "Implement a fault-tolerant Spring Boot API gateway using Resilience4j circuit breaker, sliding window metrics, and rate limiting with Bucket4j. Provide source code and architectural documentation."
    },
    {
      id: "asg-02",
      courseId: "AI-501",
      courseName: "AI-501 Neural Networks",
      title: "Multi-Head Attention Layer Implementation in PyTorch",
      dueDate: "Oct 18, 2026",
      status: "SUBMITTED",
      submittedDate: "Oct 08, 2026",
      points: 100,
      grade: "98/100",
      feedback: "Exceptional matrix broadcast efficiency. Vectorized implementation demonstrated O(N^2) memory scaling reduction.",
      submissionsCount: 180,
      totalStudents: 198,
      description: "Build custom Multi-Head Attention without torch.nn.MultiheadAttention. Benchmark latency against standard PyTorch kernels."
    },
    {
      id: "asg-03",
      courseId: "SEC-410",
      courseName: "SEC-410 Enterprise Cybersecurity",
      title: "OWASP ASVS Compliance Audit & Threat Model",
      dueDate: "Oct 24, 2026",
      status: "PENDING",
      points: 80,
      submissionsCount: 42,
      totalStudents: 88,
      description: "Analyze the LMS REST API endpoints against OWASP Top 10 guidelines and produce an automated DAST verification report."
    }
  ],

  quizzes: [
    {
      id: "qz-01",
      title: "Spring Security 6 & OAuth2 Architecture Examination",
      courseName: "CS-482 Cloud Architecture",
      timeLimit: 15,
      questionsCount: 4,
      questions: [
        {
          id: "q1",
          question: "Which component in Spring Security 6 holds the security context of the current authenticated principal thread?",
          options: [
            "SecurityContextHolder",
            "AuthenticationManager",
            "FilterChainProxy",
            "DelegatingFilterProxy"
          ],
          correctIndex: 0,
          explanation: "SecurityContextHolder uses ThreadLocal by default to associate the Authentication object with the current execution thread."
        },
        {
          id: "q2",
          question: "Why should Flyway manage database migrations rather than Hibernate's ddl-auto=update in production?",
          options: [
            "Hibernate does not support foreign keys",
            "Flyway provides deterministic, version-controlled, auditable migration history that works in multi-node clusters",
            "Flyway is faster than in-memory H2 queries",
            "Hibernate ddl-auto cannot create tables"
          ],
          correctIndex: 1,
          explanation: "Flyway runs idempotent, linear SQL scripts tracked in a schema history table, preventing schema drifts across clustered environments."
        },
        {
          id: "q3",
          question: "What is the primary security defense of using HTTP-Only and SameSite=Strict cookies for auth tokens?",
          options: [
            "Prevents Cross-Site Scripting (XSS) access to tokens and neutralizes Cross-Site Request Forgery (CSRF)",
            "Allows cross-origin iframe embedding",
            "Increases HTTPS handshake speed",
            "Enables client-side JavaScript localStorage persistence"
          ],
          correctIndex: 0,
          explanation: "HttpOnly cookies cannot be accessed by document.cookie (preventing token theft via XSS), while SameSite prevents unauthorized cross-origin requests."
        },
        {
          id: "q4",
          question: "In standard token bucket rate limiting (Bucket4j), what occurs when incoming request tokens exceed bucket capacity?",
          options: [
            "The request is dropped or rejected with HTTP 429 Too Many Requests",
            "The server restarts automatically",
            "The database connection pool closes",
            "The user account is permanently deleted"
          ],
          correctIndex: 0,
          explanation: "When tokens are depleted, subsequent requests are rate-limited with HTTP 429 until the refill rate adds new tokens."
        }
      ]
    }
  ],

  departments: [
    { id: "dep-1", name: "Computer Science & Engineering", code: "CSE", facultyCount: 28, studentCount: 840, head: "Dr. Robert Vance" },
    { id: "dep-2", name: "Artificial Intelligence & Data Science", code: "AIDS", facultyCount: 18, studentCount: 520, head: "Dr. Maya Patel" },
    { id: "dep-3", name: "Cybersecurity & Information Assurance", code: "CIA", facultyCount: 14, studentCount: 380, head: "Dr. Alistair Chen" },
    { id: "dep-4", name: "Business Administration & Analytics", code: "BAA", facultyCount: 22, studentCount: 690, head: "Dr. Sarah Jenkins" }
  ],

  academicTerms: [
    { id: "term-1", name: "Fall 2026 Semester", status: "CURRENT", startDate: "2026-08-20", endDate: "2026-12-15", coursesActive: 64 },
    { id: "term-2", name: "Spring 2027 Semester", status: "UPCOMING", startDate: "2027-01-10", endDate: "2027-05-20", coursesActive: 0 },
    { id: "term-3", name: "Summer 2026 Session", status: "COMPLETED", startDate: "2026-06-01", endDate: "2026-08-10", coursesActive: 28 }
  ],

  auditEvents: [
    { id: "aud-01", actor: "admin@optout.edu", action: "SYSTEM_DIAGNOSTICS_RUN", resource: "SPRING_BOOT_ACTUATOR", ip: "127.0.0.1", time: "Just now", status: "SUCCESS" },
    { id: "aud-02", actor: "prof.elena@optout.edu", action: "COURSE_MODULE_PUBLISHED", resource: "CS-482 / Mod 2", ip: "192.168.1.45", time: "12 mins ago", status: "SUCCESS" },
    { id: "aud-03", actor: "student@optout.edu", action: "ASSIGNMENT_SUBMISSION", resource: "AI-501 / Asg 2", ip: "10.0.0.82", time: "2 hours ago", status: "SUCCESS" },
    { id: "aud-04", actor: "system", action: "FLYWAY_SCHEMA_VERIFIED", resource: "lms_db", ip: "localhost", time: "3 hours ago", status: "SUCCESS" },
    { id: "aud-05", actor: "security_monitor", action: "RATE_LIMIT_CHECK", resource: "LOGIN_ENDPOINT", ip: "203.0.113.19", time: "5 hours ago", status: "NEUTRALIZED" }
  ]
};

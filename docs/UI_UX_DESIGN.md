# UI/UX Design System Specification
## Opt-Out University Learning Management System (LMS)

---

## 1. Design System Philosophy

The Opt-Out LMS interface is designed with a **modern university SaaS aesthetic**:
- **Light Theme as First-Class Default**: High-legibility, crisp slate backgrounds, subtle border separators, and high-contrast typography.
- **Role-Specialized Workspaces**: Rather than a generic monolithic dashboard, Students, Instructors, and Administrators receive tailored workspaces crafted around their daily workflows.
- **Enterprise Polish**: Consistent 8px grid system, rounded corners (`rounded-lg`, `rounded-xl`), smooth micro-interactions, and accessible states (focus rings, loading skeletons).
- **Glassmorphism & Depth Accents**: Soft elevations (`shadow-sm`, `shadow-md`), backdrop blurs for sticky headers and modal overlays.

---

## 2. Color Palette & Typography

### 2.1 Color Tokens (Tailwind CSS Mapping)

| Semantic Token | Tailwind Utility | Hex / HSL | Usage |
| :--- | :--- | :--- | :--- |
| **Primary (Academic Indigo)** | `bg-indigo-600` / `text-indigo-600` | `#4F46E5` | Primary buttons, active navigation, focus rings |
| **Primary Foreground** | `text-white` | `#FFFFFF` | Text on primary brand buttons |
| **Secondary (Slate Accent)** | `bg-slate-100` / `text-slate-900` | `#F1F5F9` | Secondary actions, badges, subtle highlights |
| **Canvas / Background** | `bg-slate-50` / `bg-white` | `#F8FAFC` / `#FFFFFF` | Main application shell and card backgrounds |
| **Surface Borders** | `border-slate-200` | `#E2E8F0` | Dividers, card borders, table line rules |
| **Success (Green)** | `bg-emerald-500` / `text-emerald-700` | `#10B981` | Passing grades, submitted statuses, high attendance |
| **Warning (Amber)** | `bg-amber-500` / `text-amber-700` | `#F59E0B` | Upcoming deadlines, late submissions, pending review |
| **Destructive (Rose)** | `bg-rose-500` / `text-rose-700` | `#F43F5E` | Failed grades, overdue assignments, danger actions |
| **AI Accent (Violet/Cyan)** | `bg-gradient-to-r from-violet-600 to-indigo-600` | Gradient | AI Assistant triggers, AI recommendations |

### 2.2 Typography
- **Primary Body & Display**: `Inter, system-ui, -apple-system, sans-serif`
- **Monospace (Code / ID Numbers)**: `JetBrains Mono, Fira Code, monospace`
- **Scale**:
  - Page Titles: `text-2xl font-bold tracking-tight text-slate-900`
  - Section Headers: `text-lg font-semibold text-slate-800`
  - Body Text: `text-sm font-normal text-slate-600 leading-relaxed`
  - Caption / Metadata: `text-xs font-medium text-slate-500`

---

## 3. Layout Structure & Navigation

### 3.1 Common Shell Architecture
- **Persistent Left Sidebar (Collapsible)**:
  - University Logo & Brand (`Opt-Out LMS`)
  - Role-specific navigation links with active indicators
  - User profile drawer & fast sign-out
- **Top Application Bar**:
  - Global Search (quick jump to courses, assignments, people)
  - Academic Term Selector (e.g., `Fall 2026`)
  - Notification Popover (bell icon with unread badge)
  - Direct AI Assistant Launch button
- **Main Viewport**:
  - Breadcrumb trail
  - Action header (Page title + primary call-to-action buttons)
  - Content canvas wrapped in max-width containers (`max-w-7xl mx-auto`)

---

## 4. Role-Specific Portal Experiences

### 4.1 Student Portal
- **Dashboard Highlights**:
  - Four quick-stat cards: Enrolled Courses (e.g., 5), Cumulative GPA (e.g., 3.82), Attendance Rate (e.g., 94%), Next Assignment Deadline (Countdown).
  - Weekly schedule grid / Timetable visualizer.
  - Active course cards with progress rings (`72% completed`).
  - Integrated "Ask AI" floating assistant docked at bottom-right.
- **Classroom Player**:
  - Split screen: Left 70% video player or PDF viewer; Right 30% collapsible course syllabus tree, lesson notes, and instant Q&A.
- **Quiz Experience**:
  - Focus mode: Clean, distraction-free layout with sticky top timer, progress bar, question navigator palette, and instant answer persistence.

### 4.2 Instructor Portal
- **Dashboard Highlights**:
  - Submissions awaiting grading queue (with direct "Review" actions).
  - Live class schedule for today.
  - Quick action: "Create Assignment", "Upload Materials", "Start Attendance".
- **Course Studio**:
  - Drag-and-drop module organizer.
  - Inline video/PDF upload dropzone with upload progress bars.
- **Grading Queue & Split Reviewer**:
  - Left pane: Student submission document (PDF or code).
  - Right pane: Rubric scoring sliders, AI-drafted feedback suggestions, and final approval button.

### 4.3 Administrator Portal
- **Dashboard Highlights**:
  - University-wide enrollment stats, active faculty/student headcount.
  - System health indicator (Database status, API latency, Flyway version).
  - Live security audit stream.
- **Management Grids**:
  - Advanced data tables with multi-column sorting, search filters, bulk actions (Export CSV, Deactivate), and pagination controls.

---

## 5. Component Library & Accessibility (shadcn/ui + Tailwind)
- Form inputs powered by `React Hook Form` and validated client-side with `Zod` schemas before submission.
- Accessible modals and dialogs with focus trapping and `ESC` dismissal (`@radix-ui/react-dialog`).
- Responsive tables with horizontal scroll and fixed header rows for large datasets.
- Clean charts via `Recharts` (Grade distributions, enrollment trends, attendance rates).

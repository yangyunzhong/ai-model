---
name: trellis-before-dev
description: "Discovers and injects project-specific coding guidelines from .trellis/spec/ before implementation begins. Reads spec indexes, pre-development checklists, and shared thinking guides for the target package. Use when starting a new coding task, before writing any code, switching to a different package, or needing to refresh project conventions and standards."
---

Read the relevant development guidelines before starting your task.

Special rule for this repository:

- For any page-oriented work, always read `.trellis/spec/prototype/index.md` first before any implementation. This includes prototype pages, showcase pages, DemoKit pages, PM/UI mock pages, business page revamps, and screenshot/text-to-page tasks.
- After reading `prototype/index.md`, continue with the relevant frontend/backend/guides specs for the package you are modifying.

Execute these steps:

1. **Discover packages and their spec layers**:
   ```bash
   python ./.trellis/scripts/get_context.py --mode packages
   ```

2. **Identify which specs apply** to your task based on:
   - Which package you're modifying (e.g., `cli/`, `docs-site/`)
   - What type of work (backend, frontend, unit-test, docs, etc.)

3. **Read the spec index** for each relevant module:
   ```bash
   cat .trellis/spec/<package>/<layer>/index.md
   ```
   Follow the **"Pre-Development Checklist"** section in the index.

4. **If the task is page-oriented, read prototype spec first**:
   ```bash
   cat .trellis/spec/prototype/index.md
   ```
   Treat this as mandatory for any page-oriented work before reading other implementation specs.

5. **Read the specific guideline files** listed in the Pre-Development Checklist that are relevant to your task. The index is NOT the goal — it points you to the actual guideline files (e.g., `error-handling.md`, `conventions.md`, `mock-strategies.md`). Read those files to understand the coding standards and patterns.

6. **Always read shared guides**:
   ```bash
   cat .trellis/spec/guides/index.md
   ```

7. Understand the coding standards and patterns you need to follow, then proceed with your development plan.

This step is **mandatory** before writing any code. For page-oriented work in this repository, reading `.trellis/spec/prototype/index.md` is part of that mandatory gate.

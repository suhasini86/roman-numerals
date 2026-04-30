AI Usage Explanation

## Overview  
This project was developed using AI as a primary accelerator, in line with the hiring manager's guidance to leverage AI extensively. The objective was not only to deliver a correct solution, but to demonstrate a disciplined, transparent, and production-oriented approach to AI-assisted engineering.

AI was used for ideation, scaffolding, and iteration. However, all final decisions—architecture, validation, observability scope, and API compliance—were made deliberately, with full manual review and testing.

## AI Tools Used  
● ChatGPT (prompting, design discussions)

● GitHub Copilot premium models - Claude Opus 4.6
- Code generation, suggestions, test case generation, and refactoring recommendations.
- Code review and validation were performed manually by me, without AI assistance.

## AI-Assisted Development Workflow

### Architecture Exploration  
● AI was used to evaluate possible project structures  
    ● Multiple options were compared (flat vs layered)  
    ● Final structure was selected based on maintainability and clarity

### Core Logic (Roman Numeral Conversion)  
● AI generated an initial greedy algorithm  
● Logic was manually reviewed and validated against Roman numeral rules:  
        ○ Correct symbol mapping  
        ○ Subtractive notation (IV, IX, XL, XC, etc.)  
● Refactored to improve readability and ensure deterministic behavior

No external libraries were used, as required.

### API Implementation  
● AI generated initial controller scaffolding  
● I enforced:  
        ○ Strict adherence to API contract  
        ○ Clean separation of validation and business logic  
        ○ Consistent response structure

### Error Handling  
● AI suggested general patterns  
● Implemented:  
        ○ Centralized exception handling  
        ○ Clear error messaging  
        ○ Consistent failure responses

Design favors simplicity and predictability over excessive abstraction.

### Testing  
● AI generated baseline test scenarios  
● I extended coverage to include:  
        ○ Boundary values (1, 255)  
        ○ Subtractive edge cases (4, 9, 40, 90)  
        ○ Invalid inputs (missing, non-numeric, out-of-range)

Testing was used as the primary validation mechanism for AI-generated logic.

## Prompting Strategy
Prompting evolved from broad to highly constrained:

### Initial Prompts
General requests (e.g., “As a senior developer create a Roman numeral converter microservice with Spring Boot and Java17”)

### Refined Prompts  
● Constraint-driven:  
        ○ No external libraries  
        ○ Spring Boot structure  
        ○ Strict API contract  
        ○ Logging and metrics requirements
        ○ Edge case handling

Key Learning - Precise prompts significantly improved output quality and reduced rework.  
    Iterative prompting was essential for aligning AI output with requirements.

## Validation & Quality Control  
All AI-generated outputs were subject to:  
       ● Manual code review  
       ● Unit and integration testing  
       ● Requirement verification

No code was accepted without validation. AI was treated as a generator, not a source of truth.

## Responsibility Split

### AI Contributions  
● Code scaffolding  
    ● Algorithm suggestions  
    ● Test case generation  
    ● Tooling recommendations

### My Contributions  
● Final architecture decisions  
● Input validation design  
    ● Error handling strategy  
    ● Code refinement and simplification  
    ● End-to-end validation

## Tradeoffs & Decisions

### Simplicity vs Capability  
● Implemented full observability (logs, metrics, tracing)

### Speed vs Control  
● AI accelerated development significantly  
● Manual validation ensured correctness and maintainability

## Risks of AI Usage & Mitigation

### Risks  
● Incorrect or incomplete logic  
● Overly complex generated code  
● Misalignment with requirements

### Mitigation  
● Iterative prompt refinement  
● Manual validation at each stage  
● Strong test coverage

## Conclusion  
This project demonstrates a pragmatic and controlled use of AI in software development. AI was leveraged extensively to improve speed and coverage, while human oversight ensured correctness, clarity, and alignment with engineering best practices.

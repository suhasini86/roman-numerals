AI Usage Explanation

## Overview  
This project was developed using AI as a primary accelerator, in line with the hiring manager's guidance to leverage AI extensively. The objective was not only to deliver a correct solution, but to demonstrate a disciplined, transparent, and production-oriented approach to AI-assisted engineering.

AI was utilized for ideation, scaffolding, and iteration. However, all final decisions—including architectural patterns, validation logic, observability scope, and API compliance—were made deliberately through manual review and testing.

## AI Tools Used  

● ChatGPT: Utilized for high-level architectural ideation, prompt refinement, and design decisions.

● GitHub Copilot (Claude Opus 4.6): Utilized for code generation, test case baselines, and refactoring recommendations.

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
        ○ Consistent response structure`

### Error Handling  
    ● AI suggested general patterns  
    ● I implemented:  
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

My prompting strategy evolved iteratively from broad requests (e.g., "Create a Roman numeral converter microservice with Spring Boot") to highly constraint-driven prompts specifying use of no external libraries, Spring Boot structure, strict API contracts, explicit observability requirements and handling edge cases.

Treating the AI strictly as a generator rather than a source of truth, no code was accepted without validation. All outputs were subjected to manual code review, unit testing, and requirement verification.

Key learnings - Precise prompts significantly improved output quality and reduced rework.
Iterative prompting was essential for aligning AI output with requirements.

## Responsibility Split

### AI Contributions  
● Generated initial project scaffolding

● Suggested algorithm (greedy approach for Roman Numeral Conversion)
    
● Assisted in design exploration and alternative approaches

● Produced baseline test cases

● Provided refactoring suggestions and best practices

● Recommended tooling and observability patterns


### My Contributions  
● Finalized architecture and project structure decisions  

● Designed input validation and API contract enforcement  

● Implemented error handling strategy and response consistency  

● Reviewed and corrected AI-generated logic

● Refined code for readability and simplicity

● Validated test coverage (edge cases, boundaries, invalid inputs)

● Performed end-to-end testing and verification



## Tradeoffs & Decisions

While AI significantly accelerated development speed, it introduced risks of overly complex generated code, misalignment from the requirements and subtle logical inaccuracies. To mitigate this, I prioritized iterative prompt refinement, manual validation and strong test coverage at every stage.

## Conclusion  
This project demonstrates a pragmatic and controlled use of AI in software development. AI was treated as a generator and assistant, not a source of truth. AI was leveraged extensively to improve speed and coverage, while human oversight ensured correctness, clarity, and alignment with engineering best practices.

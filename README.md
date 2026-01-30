# 📌 Table of Contents

- [General Introduction](#general-introduction)
- [Repository Structure](#repository-structure)
- [Requirements](#requirements)
- [Docker Environment](#docker-environment)
- [Usage](#usage)

# 📖 General Introduction

This repository contains the complete replication package for our ISSTA2026 submission. It includes the experimental datasets, source code, prompt templates, and evaluation results that are requested by the replication.

# 📁 Repository Structure

```
.
├── data  - LLM-generated code and the outputs of evaluated approaches.
├── implementation  - Source code of Magic and the baseline approaches.
├── prompts  - Prompt templates used to construct knowledge base and fix code issues.
├── results  - Evaluation results of Magic and the baseline approaches.
└── README.md
```

# 🛠 Requirements

- **Java Development Kit (JDK):** 17.0.5 or newer.
- **Apache Maven:** 3.8.1 or newer.
- **Python:** 3.8.5 or newer (Required for ASAT integration)
  - Install Python dependencies: `pip install -r requirements.txt`
- **Docker:** Required for the CoderEval execution environment.

# 🐳 Docker Environment

Our evaluation relies on the **CoderEval** benchmark to execute and verify code snippets in a docker environment.

1. **Clone & Setup CoderEval:**

   Follow the instructions at [CoderEval GitHub](https://github.com/CoderEval/CoderEval).

2. **Docker Image:**

   Ensure the CoderEval Docker container is built and running, as Magic interfaces with this container to run test cases.

3. **Deploying Magic**：

   Copy the Magic replication package into the running CoderEval container.

   Execute the following command to deploy the implementation:

   `docker cp implementation/CoderEval/magic_replicationpackage [CONTAINER_ID]:/home/`

# 🚀 Usage

### 1. Setup

Clone replicate package to your local file system:

`git clone https://github.com/Magic-Framework/ReplicationPackage.git`

### 2. Configuration

Before running the experiments, you must configure the following paths and keys:

- **LLM API Access:**

  Open `magic/config/ApiKeys.java` and enter your **DeepSeek API Key**.

- **ASAT Paths:**

  In `magic/tool/PylintChecker.java` and `Flake8Checker.java`, update the `TOOL_PATH` constant to point to your local installations of Pylint and Flake8.

  > *Tip: Use `which pylint` or `where pylint` in your terminal to find the absolute path.*

### 3. Run Magic (Inference & Fixing)

To execute the automated code issue mitigation:

- Run the main class: `magic/experiment/Magic.java`.
- This will process the LLM-generated code in `data/`, apply Magic to fix code issues, and output results.

### 4. Replicate Results (Evaluation)

To generate the metrics (unresolved issues, errors, etc.) reported in the paper:

- Run the evaluation class: `magic/experiment/Evaluation.java`.
- This script outputs the results of Magic and stored them in `data/`.

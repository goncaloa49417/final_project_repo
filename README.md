# Robust Web Scraping Bot with Local LLM Models

# What is this project

This repository contains 2 demo web scraping bots, which are reinforced by local LLM models whose final purpose is to generate new CSS selectors. The scraping bots were made in Kotlin, using the Selenium tools for scraping and the HTTP4k Client for making requests to the Ollama API.

The 2 available demo bots are:

- Custom Site Bot: Playground to test the bot's robustness.
- Trivago Bot: Real site to test the bot's capability on large HTML pages. Changes to the elements inside the `css selectors.json` file are required for testing purposes, as direct application of changes to the site is not possible.

# Why this project

Companies are familiar with using web scraping for an easy way to keep track on rival companies' pricing and costumar feedback in order to detect new opportunities. One of the most recurring challenges of using a bot to extract information from a website is any changes done to the HTML code of a page, structural or not. If any change breaks an used xpath or css selector to reach a certan element that element can no longer be accessed, to solve said problem, human intervention is needed to fix the issue.

So we are proposing using LLM models to fix small HTML code changes like:

- Changes of attributes in an element
- Erasure of attributes in an element
- Change of tag on wanted element
- Change of element location

By generating new css selectors with local LLM models.

Why local LLM models?

A company using an open-source LLM service is still bound to a third party if the model is accessed remotely. Companies generally prefer full control over assets critical to their operations. Therefore, by experimenting with local LLMs, we aim to validate whether it's feasible to solve the problem without incurring the high hardware and energy costs typically associated with running large models.

# How to used it

To run an available demo bot, all you need to do is clone the project, install Ollama on your PC, download the required models:
Go to cmd and run : 
- ollama run gemma3:12b 
- ollama run llama3.2:3b-instruct-fp16
- ollama run mistral-nemo

Then, to create the models used in the bots, use the command "ollama create <name of model> -f <directory of the file where the desired Modelfile is located>. The names of the templates and their Modelfiles can be found in the "model-files-llms" folder.

Models to create to use trivago bot:
- mistral-nemo-prunning
- gemma3-12b-css-trivago

Model to create to use custom bot:
- gemma3-12b-css-general
Once you have done this, you need to edit the config.properties file to point to mainPage.html in the custom site folder.

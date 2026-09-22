# Models

This folder is where any locally-trained or fine-tuned artifacts would
live (e.g. a lightweight recommendation model, embeddings cache, or a
fine-tuned checkpoint for assessment generation).

The current implementation calls a hosted LLM provider (see `app.ai.*`
settings in `backend/src/main/resources/application.properties`), so
no local model files ship in this repository by default. If you add
one:

- Keep binary weights out of Git (add to `.gitignore`) and document
  how to download/regenerate them here.
- Note the expected input/output shape and which service
  (`chatbot`, `recommendation`, `assessment`, `personalization`)
  consumes it.

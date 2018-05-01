PORT ?= 8000

# Run a test server on port 8000.
run:
	../manage.py runserver $(PORT)

# Clean up intermediate files.
# May be easier with "git clean".
clean:
	find . -name \*.pyc -type f -delete
	find . -name __pycache__ -type d -delete

# Run any pending database migrations, to get to the most recent schema.
migrate:
	../manage.py migrate


# Generate any additional schema migrations to bring the database schema back
# in sync with the model definitions.
migrations:
	../manage.py makemigrations
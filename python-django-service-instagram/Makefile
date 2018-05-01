# Set up the virtualenv etc.
setup:
	virtualenv env
	. env/bin/activate && pip install -r requirements.txt

# Run a development instance on localhost, port 8000.
run: env/bin/activate
	. env/bin/activate && make -C src run

# If the virtualenv does not exist, set it up.
#
# We can't just make env/bin/activate _depend_ on setup: that would run the
# setup every single time we try to build something that depends on
# env/bin/activate.
env/bin/activate:
	$(MAKE) setup

# Clean up virtualenv.
clean:
	rm -rf env
	make -C src clean
compile:
	lessc less/bootstrap.less > bootstrap.css

compress:
	lessc -x less/bootstrap.less > bootstrap.min.css

install:
	make clean
	make compress
	mv bootstrap.min.css ../../WebContent/css/bootstrap.min.css

clean:
	rm -f *.css
	rm -f *.js

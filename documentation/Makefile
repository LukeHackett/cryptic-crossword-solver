report:
	mkdir out/
	latexmk -bibtex -pdf main
	cp main.pdf out/report.pdf
	cp out/report.pdf pdfs/report.pdf
	make clean

books:
	for dir in `ls -d -- */`; do ( `cd $$dir`; `make`; `make clean`; `cd ..` ); done

all:
	make report
	make books
	make clean

clean:
	rm -rf out/
	latexmk -C
	rm main.bbl

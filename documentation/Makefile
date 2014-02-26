compile:
	mkdir out/
	latexmk -bibtex -pdf main
	cp main.pdf out/report.pdf
	cp out/report.pdf pdfs/report.pdf
	make clean

clean:
	rm -rf out/
	latexmk -C
	rm main.bbl

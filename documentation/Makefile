compile:
	rm -rf out/
	mkdir out/
	latexmk -C
	latexmk -bibtex -pdf main
	cp main.pdf out/report.pdf
	latexmk -C
	rm main.bbl
	cp out/report.pdf report.pdf
	rm -rf out/

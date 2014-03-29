compile:
	mkdir out/
	latexmk -bibtex -pdf main
	cp main.pdf out/main.pdf
	mv out/main.pdf ../pdfs/testing.pdf
	make clean

clean:
	rm -rf out/
	latexmk -C
	rm main.bbl

compile:
	@echo -n "compiling research document... "
	@latexmk -silent -quiet -bibtex -pdf main
	@mv main.pdf ../pdfs/research.pdf
	@echo "done!"
	@make clean

clean:
	@latexmk -silent -quiet -C
	@rm -f main.bbl
	@echo "cleaned research folder"

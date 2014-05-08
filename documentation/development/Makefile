compile:
	@echo -n "compiling development document... "
	@latexmk -silent -quiet -bibtex -pdf main
	@mv main.pdf ../pdfs/development.pdf
	@echo "done!"
	@make clean

clean:
	@latexmk -silent -quiet -C
	@rm -f main.bbl
	@echo "cleaned development folder"

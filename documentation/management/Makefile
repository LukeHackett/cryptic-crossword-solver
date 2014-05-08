compile:
	@echo -n "compiling management document... "
	@latexmk -silent -quiet -bibtex -pdf main
	@mv main.pdf ../pdfs/management.pdf
	@echo "done!"
	@make clean

clean:
	@latexmk -silent -quiet -C
	@rm -f main.bbl
	@echo "cleaned management folder"
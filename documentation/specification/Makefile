compile:
	@echo -n "compiling specification document... "
	@latexmk -silent -quiet -bibtex -pdf main
	@mv main.pdf ../pdfs/specification.pdf
	@echo "done!"
	@make clean

clean:
	@latexmk -silent -quiet -C
	@rm -f main.bbl
	@echo "cleaned specification folder"

compile:
	@echo -n "compiling design document... "
	@latexmk -silent -quiet -bibtex -pdf main
	@mv main.pdf ../pdfs/design.pdf
	@echo "done!"
	@make clean

clean:
	@latexmk -silent -quiet -C
	@rm -f main.bbl
	@echo "cleaned design folder"

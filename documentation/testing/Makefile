compile:
	@echo -n "compiling testing document... "
	@latexmk -silent -quiet -bibtex -pdf main
	@mv main.pdf ../pdfs/testing.pdf
	@echo "done!"
	@make clean

clean:
	@latexmk -silent -quiet -C
	@rm -f main.bbl
	@echo "cleaned testing folder"

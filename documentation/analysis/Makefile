compile:
	@echo -n "compiling analysis document... "
	@latexmk -silent -quiet -bibtex -pdf main
	@mv main.pdf ../pdfs/analysis.pdf
	@echo "done!"
	@make clean

clean:
	@latexmk -silent -quiet -C
	@rm -f main.bbl
	@echo "cleaned analysis folder"
